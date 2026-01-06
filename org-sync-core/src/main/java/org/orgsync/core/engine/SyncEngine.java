package org.orgsync.core.engine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.orgsync.core.Constants;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.config.OrgSyncProperties;
import org.orgsync.core.config.OrgSyncProperties.DomainSpec;
import org.orgsync.core.config.OrgSyncProperties.FieldSpec;
import org.orgsync.core.config.OrgSyncYamlLoader;
import org.orgsync.core.dto.CompanyDto;
import org.orgsync.core.dto.CompanyGroupDto;
import org.orgsync.core.dto.DepartmentDto;
import org.orgsync.core.dto.DomainKey;
import org.orgsync.core.dto.DomainType;
import org.orgsync.core.dto.IntegrationDto;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.LogType;
import org.orgsync.core.dto.OrganizationCodeDto;
import org.orgsync.core.dto.ProvisionSequenceDto;
import org.orgsync.core.dto.MemberDto;
import org.orgsync.core.dto.TargetDomain;
import org.orgsync.core.dto.UserDto;
import org.orgsync.core.dto.EmployeeType;
import org.orgsync.core.dto.MemberType;
import org.orgsync.core.dto.UserStatus;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.jdbc.JdbcApplier;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.state.LogSeqRepository;
import org.orgsync.core.util.MultiLanguageUtils;

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
    private final Map<DomainType, DomainSpec> domainSpecMap = new HashMap<>();
    private final DomainSpec organizationCodeSpec;

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
        OrgSyncProperties properties = OrgSyncYamlLoader.loadFromClasspath("org-sync-spec.yml");
        this.organizationCodeSpec = properties.organizationCodeSpec()
            .orElseThrow(() -> new IllegalStateException("organization-code spec is missing"));
        properties.departmentSpec().ifPresent(spec -> domainSpecMap.put(DomainType.DEPARTMENT, spec));
        properties.userSpec().ifPresent(spec -> domainSpecMap.put(DomainType.USER, spec));
        properties.relationMemberSpec().ifPresent(spec -> domainSpecMap.put(DomainType.RELATION_MEMBER, spec));
        properties.integrationSpec().ifPresent(spec -> domainSpecMap.put(DomainType.INTEGRATION, spec));
        properties.companyGroupSpec().ifPresent(spec -> domainSpecMap.put(DomainType.COMPANY_GROUP, spec));
        properties.companySpec().ifPresent(spec -> domainSpecMap.put(DomainType.COMPANY, spec));
        domainSpecMap.put(DomainType.ORGANIZATION_CODE, organizationCodeSpec);
    }

    public void synchronizeCompany(String companyUuid, Long logSeq) {
        lockManager.withLock(companyUuid, () -> doSynchronize(companyUuid, logSeq));
    }

    private void doSynchronize(String companyUuid, Long newLogSeq) {
        //TODO: 이것도 yml로 바꾸고 내가 가져와야 한다.
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

        //TODO:companyUuid를 바탕으로 companyId 구해야 한다.
        Long companyId = jdbcApplier.getCompanyId(companyUuid);

        Map<DomainKey, Object> createObjects = new HashMap<>();
        List<LogInfoDto> updateObjects = new ArrayList<>();
        Set<DomainKey> deleteObjects = new HashSet<>();

        List<LogInfoDto> logInfoDtos = sequenceDto.logInfoList();
        for (LogInfoDto logInfoDto : logInfoDtos) {
            DomainType domainType = logInfoDto.domain();
            if(domainType == null) {
                throw new IllegalArgumentException(Constants.ERROR_PREFIX + "can not find domain type");
            }

            DomainSpec domainSpec = domainSpecMap.get(domainType);
            if (domainSpec == null) {
                continue;
            }
            if (LogType.CREATE.equals(logInfoDto.logType())) {
                Long domainId = logInfoDto.domainId();
                DomainKey domainKey = new DomainKey(domainType, domainId);
                Object object = createObjects.getOrDefault(domainKey, instantiateDto(domainType, companyId));
                if (object instanceof OrganizationCodeDto organizationCodeDto) {
                    organizationCodeDto.set(logInfoDto);
                    createObjects.put(domainKey, organizationCodeDto);
                } else if (object instanceof DepartmentDto departmentDto) {
                    departmentDto.set(logInfoDto);
                    createObjects.put(domainKey, departmentDto);
                } else if (object instanceof UserDto userDto) {
                    userDto.set(logInfoDto);
                    createObjects.put(domainKey, userDto);
                } else if (object instanceof MemberDto memberDto) {
                    memberDto.set(logInfoDto);
                    createObjects.put(domainKey, memberDto);
                } else if (object instanceof IntegrationDto integrationDto) {
                    integrationDto.set(logInfoDto);
                    createObjects.put(domainKey, integrationDto);
                } else if (object instanceof CompanyGroupDto companyGroupDto) {
                    companyGroupDto.set(logInfoDto);
                    createObjects.put(domainKey, companyGroupDto);
                } else if (object instanceof CompanyDto companyDto) {
                    companyDto.set(logInfoDto);
                    createObjects.put(domainKey, companyDto);
                }
            } else if (LogType.UPDATE.equals(logInfoDto.logType())) {
                updateObjects.add(logInfoDto);
            } else if (LogType.DELETE.equals(logInfoDto.logType())) {
                Long domainId = logInfoDto.domainId();
                DomainKey domainKey = new DomainKey(domainType, domainId);
                deleteObjects.add(domainKey);
            } else {
                throw new IllegalArgumentException(Constants.ERROR_PREFIX + "not support log type");
            }
        }


        createObjects.forEach((key, value) -> {
            DomainSpec domainSpec = domainSpecMap.get(key.domainType());
            if (domainSpec == null || !domainSpec.isSyncEnabled()) {
                return;
            }
            LinkedHashMap<String, Object> columnValues = buildColumnValues(key.domainType(), value);
            if (columnValues.isEmpty()) {
                return;
            }
            jdbcApplier.insertRow(domainSpec.getTableName(), columnValues);
        });

        updateObjects.forEach(logInfoDto -> {
            DomainSpec domainSpec = domainSpecMap.get(logInfoDto.domain());
            if (domainSpec == null || !domainSpec.isSyncEnabled()) {
                return;
            }
            String idColumnName = requireIdColumnName(domainSpec, logInfoDto.domain());
            FieldSpec fieldSpec = domainSpec.getFields().get(logInfoDto.fieldName());
            if (fieldSpec == null || !fieldSpec.isEnabled() || fieldSpec.getColumnName() == null) {
                return;
            }
            Object updatedValue = convertUpdatedValue(logInfoDto.domain(), logInfoDto);
            jdbcApplier.updateColumn(domainSpec.getTableName(), idColumnName, logInfoDto.domainId(),
                fieldSpec.getColumnName(), updatedValue);
        });

        deleteObjects.forEach(domainKey -> {
            DomainSpec domainSpec = domainSpecMap.get(domainKey.domainType());
            if (domainSpec == null || !domainSpec.isSyncEnabled()) {
                return;
            }
            String idColumnName = requireIdColumnName(domainSpec, domainKey.domainType());
            jdbcApplier.deleteRow(domainSpec.getTableName(), idColumnName, domainKey.domainId());
        });

    }

    private Object instantiateDto(DomainType domainType, Long companyId) {
        return switch (domainType) {
            case ORGANIZATION_CODE -> new OrganizationCodeDto(companyId);
            case DEPARTMENT -> new DepartmentDto(companyId);
            case USER -> new UserDto(companyId);
            case RELATION_MEMBER -> new MemberDto();
            case INTEGRATION -> new IntegrationDto();
            case COMPANY_GROUP -> new CompanyGroupDto();
            case COMPANY -> new CompanyDto();
            default -> null;
        };
    }

    private LinkedHashMap<String, Object> buildColumnValues(DomainType domainType, Object dto) {
        DomainSpec domainSpec = domainSpecMap.get(domainType);
        LinkedHashMap<String, Object> columnValues = new LinkedHashMap<>();
        if (domainSpec == null) {
            return columnValues;
        }
        for (Map.Entry<String, FieldSpec> entry : domainSpec.getFields().entrySet()) {
            String fieldName = entry.getKey();
            FieldSpec fieldSpec = entry.getValue();
            if (!fieldSpec.isEnabled()) {
                continue;
            }
            Object value = extractFieldValue(domainType, dto, fieldName);
            if (value != null && fieldSpec.getColumnName() != null) {
                columnValues.put(fieldSpec.getColumnName(), value);
            }
        }
        return columnValues;
    }

    private Object extractFieldValue(DomainType domainType, Object dto, String fieldName) {
        return switch (domainType) {
            case ORGANIZATION_CODE -> {
                OrganizationCodeDto organizationCodeDto = (OrganizationCodeDto) dto;
                yield switch (fieldName) {
                    case "id" -> organizationCodeDto.getId();
                    case "code" -> organizationCodeDto.getCode();
                    case "type" -> organizationCodeDto.getType() != null ? organizationCodeDto.getType().name() : null;
                    case "name" -> organizationCodeDto.getName();
                    case "sortOrder" -> organizationCodeDto.getSortOrder();
                    case "multiLanguageDtoMap", "multiLanguageMap" -> organizationCodeDto.getMultiLanguageDtoMap();
                    case "companyId" -> organizationCodeDto.getCompanyId();
                    default -> null;
                };
            }
            case DEPARTMENT -> {
                DepartmentDto departmentDto = (DepartmentDto) dto;
                yield switch (fieldName) {
                    case "id" -> departmentDto.getId();
                    case "companyId", "company_id" -> departmentDto.getCompanyId();
                    case "name" -> departmentDto.getName();
                    case "parent", "parentId" -> departmentDto.getParentId();
                    case "sort_order", "sortOrder" -> departmentDto.getSortOrder();
                    case "code" -> departmentDto.getCode();
                    case "alias" -> departmentDto.getAlias();
                    case "emailId" -> departmentDto.getEmailId();
                    case "status" -> departmentDto.getStatus();
                    case "departmentPath" -> departmentDto.getDepartmentPath();
                    case "multiLanguageDtoMap", "multiLanguageMap" -> departmentDto.getMultiLanguageDtoMap();
                    default -> null;
                };
            }
            case USER -> {
                UserDto userDto = (UserDto) dto;
                yield switch (fieldName) {
                    case "id" -> userDto.getId();
                    case "companyId", "company_id" -> userDto.getCompanyId();
                    case "name" -> userDto.getName();
                    case "employeeNumber" -> userDto.getEmployeeNumber();
                    case "loginId" -> userDto.getLoginId();
                    case "locale" -> userDto.getLocale();
                    case "status" -> userDto.getStatus() != null ? userDto.getStatus().name() : null;
                    case "needOperatorAssignment" -> userDto.getNeedOperatorAssignment();
                    case "multiLanguageDtoMap", "multiLanguageMap" -> userDto.getMultiLanguageMap();
                    case "directTel" -> userDto.getDirectTel();
                    case "mobileNo" -> userDto.getMobileNo();
                    case "mobileSearch" -> userDto.getMobileSearch();
                    case "repTel" -> userDto.getRepTel();
                    case "fax" -> userDto.getFax();
                    case "selfInfo" -> userDto.getSelfInfo();
                    case "job" -> userDto.getJob();
                    case "location" -> userDto.getLocation();
                    case "homePage" -> userDto.getHomePage();
                    case "messenger" -> userDto.getMessenger();
                    case "birthday" -> userDto.getBirthday();
                    case "lunarCalendar" -> userDto.isLunarCalendar();
                    case "anniversary" -> userDto.getAnniversary();
                    case "address" -> userDto.getAddress();
                    case "memo" -> userDto.getMemo();
                    case "externalEmail" -> userDto.getExternalEmail();
                    case "joinDate" -> userDto.getJoinDate();
                    case "recognizedJoinDate" -> userDto.getRecognizedJoinDate();
                    case "residentRegistrationNumber" -> userDto.getResidentRegistrationNumber();
                    case "employeeType" -> userDto.getEmployeeType() != null ? userDto.getEmployeeType().name() : null;
                    case "positionCode" -> userDto.getPositionCode();
                    case "gradeCode" -> userDto.getGradeCode();
                    case "userGroupUserList" -> userDto.getUserGroupUserList();
                    case "integration" -> userDto.getIntegration();
                    default -> null;
                };
            }
            case RELATION_MEMBER -> {
                MemberDto memberDto = (MemberDto) dto;
                yield switch (fieldName) {
                    case "id" -> memberDto.getId();
                    case "user" -> memberDto.getUser();
                    case "department" -> memberDto.getDepartment();
                    case "dutyCode" -> memberDto.getDutyCode();
                    case "memberType" -> memberDto.getMemberType() != null ? memberDto.getMemberType().name() : null;
                    case "sortOrder" -> memberDto.getSortOrder();
                    case "departmentOrder" -> memberDto.getDepartmentOrder();
                    default -> null;
                };
            }
            case INTEGRATION -> {
                IntegrationDto integrationDto = (IntegrationDto) dto;
                yield "id".equals(fieldName) ? integrationDto.getId() : null;
            }
            case COMPANY_GROUP -> {
                CompanyGroupDto companyGroupDto = (CompanyGroupDto) dto;
                yield "id".equals(fieldName) ? companyGroupDto.getId() : null;
            }
            case COMPANY -> {
                CompanyDto companyDto = (CompanyDto) dto;
                yield switch (fieldName) {
                    case "id" -> companyDto.getId();
                    case "companyUuid", "uuid" -> companyDto.getUuid();
                    case "companyGroup" -> companyDto.getCompanyGroup();
                    default -> null;
                };
            }
            default -> null;
        };
    }

    private Object convertUpdatedValue(DomainType domainType, LogInfoDto logInfoDto) {
        String fieldName = logInfoDto.fieldName();
        Object updatedValue = logInfoDto.updatedValue();
        if (updatedValue == null) {
            return null;
        }

        return switch (domainType) {
            case ORGANIZATION_CODE -> switch (fieldName) {
                case "id" -> Long.valueOf(updatedValue.toString());
                case "code", "name" -> updatedValue.toString();
                case "type" -> updatedValue.toString();
                case "sortOrder" -> Integer.valueOf(updatedValue.toString());
                case "multiLanguageDtoMap", "multiLanguageMap" ->
                    MultiLanguageUtils.parseJson(logInfoDto.domainId(), TargetDomain.ORGANIZATION_CODE,
                        updatedValue.toString());
                default -> updatedValue;
            };
            case DEPARTMENT -> switch (fieldName) {
                case "id", "companyId", "company_id", "parent", "parentId" -> Long.valueOf(updatedValue.toString());
                case "sort_order", "sortOrder" -> Integer.valueOf(updatedValue.toString());
                case "multiLanguageDtoMap", "multiLanguageMap" -> MultiLanguageUtils.parseJson(logInfoDto.domainId(),
                    TargetDomain.DEPARTMENT, updatedValue.toString());
                default -> updatedValue.toString();
            };
            case USER -> switch (fieldName) {
                case "id", "companyId", "company_id", "positionCode", "gradeCode", "integration" ->
                    Long.valueOf(updatedValue.toString());
                case "status" -> UserStatus.valueOf(updatedValue.toString());
                case "needOperatorAssignment", "lunarCalendar" -> Boolean.valueOf(updatedValue.toString());
                case "employeeType" -> EmployeeType.valueOf(updatedValue.toString());
                case "birthday", "anniversary", "joinDate", "recognizedJoinDate" ->
                    LocalDate.parse(updatedValue.toString());
                case "multiLanguageDtoMap", "multiLanguageMap" -> MultiLanguageUtils.parseJson(logInfoDto.domainId(),
                    TargetDomain.USER, updatedValue.toString());
                case "userGroupUserList" -> parseUserGroupList(updatedValue);
                default -> updatedValue.toString();
            };
            case RELATION_MEMBER -> switch (fieldName) {
                case "id", "user", "department", "dutyCode" -> Long.valueOf(updatedValue.toString());
                case "memberType" -> MemberType.valueOf(updatedValue.toString());
                case "sortOrder", "departmentOrder" -> Integer.valueOf(updatedValue.toString());
                default -> updatedValue;
            };
            case INTEGRATION -> Long.valueOf(updatedValue.toString());
            case COMPANY_GROUP -> Long.valueOf(updatedValue.toString());
            case COMPANY -> switch (fieldName) {
                case "id", "companyGroup" -> Long.valueOf(updatedValue.toString());
                default -> updatedValue.toString();
            };
            default -> updatedValue;
        };
    }

    private List<Long> parseUserGroupList(Object updatedValue) {
        if (updatedValue instanceof List<?> list) {
            return list.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        }
        String value = updatedValue.toString();
        if (value.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
            .filter(token -> !token.isBlank())
            .map(String::trim)
            .map(Long::valueOf)
            .collect(Collectors.toList());
    }

    private String requireIdColumnName(DomainSpec domainSpec, DomainType domainType) {
        FieldSpec idFieldSpec = domainSpec.getFields().get("id");
        if (idFieldSpec == null || !idFieldSpec.isEnabled() || idFieldSpec.getColumnName() == null) {
            throw new IllegalStateException(Constants.ERROR_PREFIX + domainType.name().toLowerCase()
                + " id column mapping is required");
        }
        return idFieldSpec.getColumnName();
    }
}
