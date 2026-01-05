package org.orgsync.core.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.orgsync.core.Constants;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.config.OrgSyncProperties;
import org.orgsync.core.config.OrgSyncProperties.FieldSpec;
import org.orgsync.core.config.OrgSyncProperties.OrganizationCodeSpec;
import org.orgsync.core.config.OrgSyncYamlLoader;
import org.orgsync.core.dto.DomainKey;
import org.orgsync.core.dto.DomainType;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.LogType;
import org.orgsync.core.dto.OrganizationCodeDto;
import org.orgsync.core.dto.ProvisionSequenceDto;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.jdbc.JdbcApplier;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.state.LogSeqRepository;

/**
 * Coordinates synchronization by pulling data from the org chart server and applying it
 * to a downstream database.
 */
public class SyncEngine {

    private final OrgChartClient client;
    private final LogSeqRepository logSeqRepository;
    private final JdbcApplier jdbcApplier;
    private final DomainEventPublisher eventPublisher;
    private final LockManager lockManager;
    private final OrganizationCodeSpec organizationCodeSpec;

    public SyncEngine(OrgChartClient client,
                      LogSeqRepository logSeqRepository,
                      JdbcApplier jdbcApplier,
                      DomainEventPublisher eventPublisher,
                      LockManager lockManager) {
        this.client = Objects.requireNonNull(client, "client");
        this.logSeqRepository = Objects.requireNonNull(logSeqRepository, "logSeqRepository");
        this.jdbcApplier = Objects.requireNonNull(jdbcApplier, "jdbcApplier");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.lockManager = Objects.requireNonNull(lockManager, "lockManager");
        OrgSyncProperties properties = OrgSyncYamlLoader.loadFromClasspath("application.yml");
        this.organizationCodeSpec = properties.organizationCodeSpec()
            .orElseThrow(() -> new IllegalStateException("organization-code spec is missing"));
    }

    public void synchronizeCompany(String companyUuid, Long logSeq) {
        lockManager.withLock(companyUuid, () -> doSynchronize(companyUuid, logSeq));
    }

    private void doSynchronize(String companyUuid, Long newLogSeq) {
        Long existedLogSeq = logSeqRepository.loadLogSeq(companyUuid).orElse(-1L);
        if (newLogSeq <= existedLogSeq) {
            return;
        }
        ProvisionSequenceDto response = client.fetchChanges(companyUuid, existedLogSeq);
        if (response.needSnapshot()) {
            applySnapshot(companyUuid, response);
        } else {
            applyDelta(companyUuid, response);
        }
        // 이거 이때 하면 안된다. 쉬바  변경해야 한다. logSeqRepository.saveCursor(companyUuid, response.nextCursor());
    }

    private void applySnapshot(String companyUuid, ProvisionSequenceDto sequenceDto) {
        // 스냅샷 데이터로부터 저장
    }

    private void applyDelta(String companyUuid, ProvisionSequenceDto sequenceDto) {


        Map<DomainKey, Object> createObjects = new HashMap<>();
        List<LogInfoDto> updateObjects = new ArrayList<>();
        Set<DomainKey> deleteObjects = new HashSet<>();

        List<LogInfoDto> logInfoDtos = sequenceDto.logInfoList();
        for (LogInfoDto logInfoDto : logInfoDtos) {
            DomainType domainType = logInfoDto.domain();
            if(domainType == null) {
                throw new IllegalArgumentException(Constants.ERROR_PREFIX + "can not find domain type");
            }

            if (DomainType.ORGANIZATION_CODE.equals(domainType)) {
                if (LogType.CREATE.equals(logInfoDto.logType())) {
                    Long domainId = logInfoDto.domainId();
                    DomainKey domainKey = new DomainKey(DomainType.ORGANIZATION_CODE, domainId);
                    Object object = createObjects.getOrDefault(domainKey, new OrganizationCodeDto());
                    OrganizationCodeDto dto = (OrganizationCodeDto) object;
                    dto.set(logInfoDto);
                    createObjects.put(domainKey, dto);
                } else if (LogType.UPDATE.equals(logInfoDto.logType())) {
                    updateObjects.add(logInfoDto);
                } else if (LogType.DELETE.equals(logInfoDto.logType())) {
                    Long domainId = logInfoDto.domainId();
                    DomainKey domainKey = new DomainKey(DomainType.ORGANIZATION_CODE, domainId);
                    deleteObjects.add(domainKey);
                } else {
                    throw new IllegalArgumentException(Constants.ERROR_PREFIX + "not support log type");
                }
            }
        }


        createObjects.forEach((key, value) -> {
            if (!(value instanceof OrganizationCodeDto organizationCodeDto)) {
                return;
            }
            if (!organizationCodeSpec.isSyncEnabled()) {
                return;
            }
            LinkedHashMap<String, Object> columnValues = buildColumnValues(organizationCodeDto);
            jdbcApplier.insertRow(organizationCodeSpec.getTableName(), columnValues);
        });

        updateObjects.forEach(logInfoDto -> {

            // TODO: jdbc를 이용해서 업데이트 해야한다.
            // TODO: 업데이트 이벤트를 날려야 한다.
        });

        deleteObjects.forEach(domainKey -> {
            //TODO: jdbc를 이용해서 삭제 해야한다.
            //TODO: 삭제 이벤트를 날려야 한다.
        });

    }

    private LinkedHashMap<String, Object> buildColumnValues(OrganizationCodeDto organizationCodeDto) {
        LinkedHashMap<String, Object> columnValues = new LinkedHashMap<>();
        for (Map.Entry<String, FieldSpec> entry : organizationCodeSpec.getFields().entrySet()) {
            String fieldName = entry.getKey();
            FieldSpec fieldSpec = entry.getValue();
            if (!fieldSpec.isEnabled()) {
                continue;
            }
            Object value = extractFieldValue(organizationCodeDto, fieldName);
            if (value != null && fieldSpec.getColumnName() != null) {
                columnValues.put(fieldSpec.getColumnName(), value);
            }
        }
        return columnValues;
    }

    private Object extractFieldValue(OrganizationCodeDto dto, String fieldName) {
        return switch (fieldName) {
            case "id" -> dto.getId();
            case "code" -> dto.getCode();
            case "type" -> dto.getType() != null ? dto.getType().name() : null;
            case "name" -> dto.getName();
            case "sortOrder" -> dto.getSortOrder();
            case "multiLanguageDtoMap", "multiLanguageMap" -> dto.getMultiLanguageDtoMap();
            default -> null;
        };
    }
}
