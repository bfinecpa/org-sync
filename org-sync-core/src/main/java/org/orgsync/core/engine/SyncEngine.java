package org.orgsync.core.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.orgsync.core.Constants;
import org.orgsync.core.client.OrgChartClient;
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

import java.util.Objects;

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
            /*
            insert into dop_user (fdf, asdf, asdf, asdf, asdf)
            values (asdf, adsf, asd, asd, asd,fads);
             */

            // TODO: create row로 바꾸고,
            // TODO: jdbc를 이용해서 저장해야 한다.
            // TODO: 생성 이벤트를 날려야 한다.
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
}
