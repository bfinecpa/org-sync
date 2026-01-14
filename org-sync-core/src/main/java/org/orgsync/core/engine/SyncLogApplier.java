package org.orgsync.core.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.orgsync.core.Constants;
import org.orgsync.core.dto.DomainKey;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.deltaDto.CompanyGroupDeltaDto;
import org.orgsync.core.dto.deltaDto.DepartmentDeltaDto;
import org.orgsync.core.dto.deltaDto.IntegrationDeltaDto;
import org.orgsync.core.dto.deltaDto.MemberDeltaDto;
import org.orgsync.core.dto.deltaDto.OrganizationCodeDeltaDto;
import org.orgsync.core.dto.deltaDto.Settable;
import org.orgsync.core.dto.deltaDto.UserDeltaDto;
import org.orgsync.core.dto.domainDto.CompanyDto;
import org.orgsync.core.dto.domainDto.CompanyGroupDto;
import org.orgsync.core.dto.domainDto.DepartmentDto;
import org.orgsync.core.dto.domainDto.IntegrationDto;
import org.orgsync.core.dto.domainDto.MemberDto;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.domainDto.OrganizationCodeDto;
import org.orgsync.core.dto.domainDto.UserDto;
import org.orgsync.core.dto.domainDto.UserGroupUserDto;
import org.orgsync.core.dto.type.DomainType;
import org.orgsync.core.dto.type.LogType;
import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.type.TargetDomain;
import org.orgsync.core.logging.SyncLogger;
import org.orgsync.core.service.OrgSyncCompanyGroupService;
import org.orgsync.core.service.OrgSyncCompanyService;
import org.orgsync.core.service.OrgSyncDepartmentService;
import org.orgsync.core.service.OrgSyncIntegrationService;
import org.orgsync.core.service.OrgSyncLogSeqService;
import org.orgsync.core.service.OrgSyncMemberService;
import org.orgsync.core.service.OrgSyncMultiLanguageService;
import org.orgsync.core.service.OrgSyncOrganizationCodeService;
import org.orgsync.core.service.OrgSyncUserGroupCodeUserService;
import org.orgsync.core.service.OrgSyncUserService;

class SyncLogApplier {

    private final OrgSyncLogSeqService logSeqService;
    private final OrgSyncOrganizationCodeService organizationCodeService;
    private final OrgSyncDepartmentService departmentService;
    private final OrgSyncUserService userService;
    private final OrgSyncMemberService memberService;
    private final OrgSyncIntegrationService integrationService;
    private final OrgSyncCompanyGroupService companyGroupService;
    private final OrgSyncCompanyService companyService;
    private final OrgSyncUserGroupCodeUserService userGroupCodeUserService;
    private final OrgSyncMultiLanguageService multiLanguageService;
    private final ObjectMapper objectMapper;
    private final SyncLogger logger;

    SyncLogApplier(OrgSyncLogSeqService logSeqService, OrgSyncOrganizationCodeService organizationCodeService,
        OrgSyncDepartmentService departmentService, OrgSyncUserService userService, OrgSyncMemberService memberService,
        OrgSyncIntegrationService integrationService, OrgSyncCompanyGroupService companyGroupService,
        OrgSyncCompanyService companyService, OrgSyncUserGroupCodeUserService userGroupCodeUserService,
        OrgSyncMultiLanguageService multiLanguageService, ObjectMapper objectMapper, SyncLogger logger) {
        this.logSeqService = logSeqService;
        this.organizationCodeService = organizationCodeService;
        this.departmentService = departmentService;
        this.userService = userService;
        this.memberService = memberService;
        this.integrationService = integrationService;
        this.companyGroupService = companyGroupService;
        this.companyService = companyService;
        this.userGroupCodeUserService = userGroupCodeUserService;
        this.multiLanguageService = multiLanguageService;
        this.objectMapper = objectMapper;
        this.logger = logger;
    }

    void applyLogInfos(String companyUuid, List<LogInfoDto> logInfoDtos, Long logSeq) {
        info(companyUuid, "Apply logInfos. logSeq :" + logSeq);
        CompanyDto companyDto = companyService.findByUuid(companyUuid);

        Map<DomainKey, Settable> createObjects = new HashMap<>();
        Map<DomainKey, Settable> updateObjects = new HashMap<>();
        Set<DomainKey> deleteObjects = new HashSet<>();


        for (LogInfoDto logInfoDto : logInfoDtos) {
            DomainType domainType = logInfoDto.domain();
            if (domainType == null) {
                throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "can not find domain type");
            }
            if (LogType.CREATE.equals(logInfoDto.logType()) && !specificFilter(logInfoDto)) {
                createDomainDto(logInfoDto, domainType, createObjects);
            } else if (LogType.UPDATE.equals(logInfoDto.logType()) || specificFilter(logInfoDto)) {
                updateDomainDto(logInfoDto, domainType, createObjects, updateObjects);
            } else if (LogType.DELETE.equals(logInfoDto.logType())) {
                deleteDomainDto(logInfoDto, domainType, deleteObjects);
            } else {
                throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "not support log type");
            }
        }

        createObjects.forEach((key, value) -> applyCreate(key.domainType(), value, companyDto));
        updateObjects.forEach((key, value) -> applyUpdate(key.domainType(), value, companyDto));
        deleteObjects.forEach(domainKey -> applyDelete(domainKey.domainType(), domainKey.domainId(), companyDto));

        logSeqService.saveLogSeq(companyDto.getId(), logSeq);
        info(companyUuid, "LogInfos applied. logSeq :" + logSeq);
    }

    private boolean specificFilter(LogInfoDto logInfoDto) {
        return DomainType.USER.equals(logInfoDto.domain()) && "userGroupUserList".equals(logInfoDto.fieldName());
    }

    private static void deleteDomainDto(LogInfoDto logInfoDto, DomainType domainType, Set<DomainKey> deleteObjects) {
        Long domainId = logInfoDto.domainId();
        DomainKey domainKey = new DomainKey(domainType, domainId);
        deleteObjects.add(domainKey);
    }

    private void updateDomainDto(LogInfoDto logInfoDto, DomainType domainType, Map<DomainKey, Settable> createObjects,
        Map<DomainKey, Settable> updateObjects) {
        Long domainId = logInfoDto.domainId();
        DomainKey domainKey = new DomainKey(domainType, domainId);
        if (createObjects.containsKey(domainKey)) {
            Settable object = createObjects.get(domainKey);
            if (DomainType.COMPANY_GROUP.equals(domainType)) {
                CompanyGroupDto companyGroupDto = companyGroupService.findById(domainId);
                if (companyGroupDto == null) {
                    createObjects.put(domainKey, object);
                    updateObjects.put(domainKey, object);
                }
            } else if (DomainType.INTEGRATION.equals(domainType)) {
                IntegrationDto integrationDto = integrationService.findById(domainId);
                if (integrationDto == null) {
                    createObjects.put(domainKey, object);
                    updateObjects.put(domainKey, object);
                }
            } else {
                object.set(logInfoDto);
                createObjects.put(domainKey, object);
            }
        } else {
            Settable object = updateObjects.getOrDefault(domainKey, instantiateDto(domainType, domainId));
            if (DomainType.COMPANY_GROUP.equals(domainType)) {
                CompanyGroupDto companyGroupDto = companyGroupService.findById(domainId);
                if (companyGroupDto == null) {
                    createObjects.put(domainKey, object);
                    updateObjects.put(domainKey, object);
                }
            } else if (DomainType.INTEGRATION.equals(domainType)) {
                IntegrationDto integrationDto = integrationService.findById(domainId);
                if (integrationDto == null) {
                    createObjects.put(domainKey, object);
                    updateObjects.put(domainKey, object);
                }
            } else {
                object.set(logInfoDto);
                updateObjects.put(domainKey, object);
            }
        }
    }

    private void createDomainDto(LogInfoDto logInfoDto, DomainType domainType, Map<DomainKey, Settable> createObjects) {
        Long domainId = logInfoDto.domainId();
        DomainKey domainKey = new DomainKey(domainType, domainId);
        Settable object = createObjects.getOrDefault(domainKey, instantiateDto(domainType, domainId));
        object.set(logInfoDto);
        createObjects.put(domainKey, object);
    }

    private Settable instantiateDto(DomainType domainType, Long domainId) {
        return switch (domainType) {
            case ORGANIZATION_CODE -> new OrganizationCodeDeltaDto(domainId);
            case DEPARTMENT -> new DepartmentDeltaDto(domainId);
            case USER -> new UserDeltaDto(domainId);
            case RELATION_MEMBER -> new MemberDeltaDto(domainId);
            case INTEGRATION -> new IntegrationDeltaDto(domainId);
            case COMPANY_GROUP -> new CompanyGroupDeltaDto(domainId);
            default -> throw new IllegalStateException(Constants.ORG_SYNC_PREFIX + "Unexpected value: " + domainType);
        };
    }

    private void applyCreate(DomainType domainType, Settable dto, CompanyDto companyDto) {
        switch (domainType) {
            case ORGANIZATION_CODE -> createOrganizationCode((OrganizationCodeDeltaDto) dto, companyDto);
            case DEPARTMENT -> createDepartment((DepartmentDeltaDto) dto, companyDto);
            case USER -> createUser((UserDeltaDto) dto, companyDto);
            case RELATION_MEMBER -> createMember((MemberDeltaDto) dto, companyDto);
            case INTEGRATION -> createIntegration((IntegrationDeltaDto) dto, companyDto);
            case COMPANY_GROUP -> createCompanyGroup((CompanyGroupDeltaDto) dto, companyDto);
            default -> throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "not support domain type in applyCreate");
        }
    }

    private void createCompanyGroup(CompanyGroupDeltaDto deltaDto, CompanyDto companyDto) {
        CompanyGroupDto companyGroupDto = new CompanyGroupDto(deltaDto.getId());
        companyGroupService.create(companyGroupDto);
        info(companyDto.getUuid(), "create company group. id: " + deltaDto.getId());
    }

    private void createIntegration(IntegrationDeltaDto deltaDto, CompanyDto companyDto) {
        IntegrationDto integrationDto = new IntegrationDto(deltaDto.getId());
        integrationService.create(integrationDto);
        info(companyDto.getUuid(), "create integration. id: " + deltaDto.getId());
    }

    private void createMember(MemberDeltaDto deltaDto, CompanyDto companyDto) {
        MemberDto memberDto = new MemberDto(
            deltaDto.getId(),
            deltaDto.getUserId(),
            deltaDto.getDepartment(),
            deltaDto.getDutyCode(),
            deltaDto.getMemberType(),
            deltaDto.getSortOrder(),
            deltaDto.getDepartmentOrder()
        );
        memberService.create(memberDto);
        info(companyDto.getUuid(), "create member. id: " + deltaDto.getId());
    }

    private void createUser(UserDeltaDto deltaDto, CompanyDto companyDto) {
        UserDto userDto = new UserDto(
            deltaDto.getId(),
            companyDto.getId(),
            deltaDto.getName(),
            deltaDto.getEmployeeNumber(),
            deltaDto.getLoginId(),
            deltaDto.getLocale(),
            deltaDto.getStatus(),
            deltaDto.getNeedOperatorAssignment(),
            deltaDto.getDirectTel(),
            deltaDto.getMobileNo(),
            deltaDto.getRepTel(),
            deltaDto.getFax(),
            deltaDto.getSelfInfo(),
            deltaDto.getJob(),
            deltaDto.getLocation(),
            deltaDto.getHomePage(),
            deltaDto.getMessenger(),
            deltaDto.getBirthday(),
            deltaDto.getLunarCalendar(),
            deltaDto.getAnniversary(),
            deltaDto.getAddress(),
            deltaDto.getMemo(),
            deltaDto.getExternalEmail(),
            deltaDto.getJoinDate(),
            deltaDto.getRecognizedJoinDate(),
            deltaDto.getResidentRegistrationNumber(),
            deltaDto.getEmployeeType(),
            deltaDto.getPositionCodeId(),
            deltaDto.getGradeCodeId()
        );
        userService.create(userDto);

        List<Long> userGroupCodeList = getList(deltaDto.getUserGroupUserList());
        for (Long userGroupCodeId : userGroupCodeList) {
            UserGroupUserDto userGroupUserDto = new UserGroupUserDto(userDto.getId(), userGroupCodeId);
            userGroupCodeUserService.create(userGroupUserDto);
        }

        List<MultiLanguageDto> multiLanguageDtos = getMultiLanguages(deltaDto.getId(), TargetDomain.USER,
            deltaDto.getMultiLanguageMap());
        List<MultiLanguageDto> multiLanguageDtosWithValue = multiLanguageDtos.stream()
            .filter(dto -> !dto.getValue().isEmpty())
            .toList();
        if (!multiLanguageDtosWithValue.isEmpty()) {
            multiLanguageService.create(multiLanguageDtosWithValue);
        }

        info(companyDto.getUuid(), "create user. id: " + deltaDto.getId());
    }

    private void createDepartment(DepartmentDeltaDto deltaDto, CompanyDto companyDto) {
        DepartmentDto departmentDto = new DepartmentDto(
            deltaDto.getId(),
            companyDto.getId(),
            deltaDto.getName(),
            deltaDto.getParentId(),
            deltaDto.getSortOrder(),
            deltaDto.getCode(),
            deltaDto.getAlias(),
            deltaDto.getEmailId(),
            deltaDto.getStatus(),
            deltaDto.getDepartmentPath()
        );

        if (departmentDto.getId() == 1459060369380098048L) {
            int test = 0;
        }

        departmentService.create(departmentDto);

        List<MultiLanguageDto> multiLanguageDtos = getMultiLanguages(deltaDto.getId(), TargetDomain.DEPARTMENT,
            deltaDto.getMultiLanguageDtoMap());
        List<MultiLanguageDto> multiLanguageDtosWithValue = multiLanguageDtos.stream()
            .filter(dto -> !dto.getValue().isEmpty())
            .toList();
        if (!multiLanguageDtosWithValue.isEmpty()) {
            multiLanguageService.create(multiLanguageDtosWithValue);
        }

        info(companyDto.getUuid(), "create department. id: " + deltaDto.getId());
    }

    private void createOrganizationCode(OrganizationCodeDeltaDto deltaDto, CompanyDto companyDto) {
        OrganizationCodeDto organizationCodeDto = new OrganizationCodeDto(
            deltaDto.getId(),
            companyDto.getId(),
            deltaDto.getCode(),
            deltaDto.getType(),
            deltaDto.getName(),
            deltaDto.getSortOrder()
        );
        organizationCodeService.create(organizationCodeDto);
        List<MultiLanguageDto> multiLanguageDtos = getMultiLanguages(deltaDto.getId(), TargetDomain.ORGANIZATION_CODE,
            deltaDto.getMultiLanguageDtoMap());
        List<MultiLanguageDto> multiLanguageDtosWithValue = multiLanguageDtos.stream()
            .filter(dto -> !dto.getValue().isEmpty())
            .toList();
        if (!multiLanguageDtosWithValue.isEmpty()) {
            multiLanguageService.create(multiLanguageDtosWithValue);
        }
        info(companyDto.getUuid(), "create organization code. id: " + deltaDto.getId());
    }

    private void applyUpdate(DomainType domainType, Settable dto, CompanyDto companyDto) {
        switch (domainType) {
            case ORGANIZATION_CODE -> updateOrganization((OrganizationCodeDeltaDto) dto, companyDto);
            case DEPARTMENT -> updateDepartment((DepartmentDeltaDto) dto, companyDto);
            case USER -> updateUser((UserDeltaDto) dto, companyDto);
            case RELATION_MEMBER -> updateMember((MemberDeltaDto) dto, companyDto);
            case INTEGRATION -> updateIntegration((IntegrationDeltaDto) dto, companyDto);
            case COMPANY_GROUP -> updateCompanyGroup((CompanyGroupDeltaDto) dto, companyDto);
            default -> throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "not support domain type in applyUpdate");
        }
    }

    private void updateCompanyGroup(CompanyGroupDeltaDto deltaDto, CompanyDto companyDto) {
        if (companyDto.getCompanyGroupId() == null) {
            companyDto.updateCompanyGroupId(deltaDto.getId());
            companyService.updateCompanyGroupId(companyDto);
            info(companyDto.getUuid(), "update company group. id: " + deltaDto.getId());
        } else {
            String companyUuids = deltaDto.getCompanyUuids();
            List<String> uuids = getList(companyUuids);
            if (!uuids.contains(companyDto.getId())) {
                companyDto.updateCompanyGroupId(null);
                companyService.updateCompanyGroupId(companyDto);
                info(companyDto.getUuid(), "update company group. id: " + deltaDto.getId());
                if (companyService.existsByCompanyGroupId(deltaDto.getId())) {
                    companyGroupService.delete(deltaDto.getId());
                    info(companyDto.getUuid(), "delete company group. id: " + deltaDto.getId());
                }
            }
        }
    }

    private void updateIntegration(IntegrationDeltaDto deltaDto, CompanyDto companyDto) {
        List<Long> userIds = getList(deltaDto.getUserIds());
        UserDto userDto = userService.findByCompanyIdAndIntegrationId(
            companyDto.getId(), deltaDto.getId());
        if (userIds.isEmpty() && userDto != null) {
            userDto = userService.findByCompanyIdAndUserIds(companyDto.getId(),
                userIds);
            userDto.updateIntegrationId(null);
            userService.updateIntegration(userDto);
            info(companyDto.getUuid(), "update integration. id: " + deltaDto.getId());

            if (userService.existsByIntegrationId(deltaDto.getId())) {
                integrationService.delete(deltaDto.getId());
                info(companyDto.getUuid(), "delete integration. id: " + deltaDto.getId());
            }
        } else if (!userIds.isEmpty() && userDto == null) {
            userDto.updateIntegrationId(deltaDto.getId());
            userService.updateIntegration(userDto);
            info(companyDto.getUuid(), "update integration. id: " + deltaDto.getId());
        }
        // 값이 한번에 바뀌는 경우가 존재할까?
    }

    private void updateMember(MemberDeltaDto deltaDto, CompanyDto companyDto) {
        MemberDto memberDto = memberService.findById(deltaDto.getId());
        if (memberDto == null) {
            return;
        }

        memberDto.update(deltaDto.getUserId(), deltaDto.getDepartment(),
            deltaDto.getDutyCode(),
            deltaDto.getMemberType(), deltaDto.getSortOrder(), deltaDto.getDepartmentOrder());
        memberService.update(memberDto);
        info(companyDto.getUuid(), "update member. id: " + deltaDto.getId());
    }

    private void updateUser(UserDeltaDto deltaDto, CompanyDto companyDto) {
        UserDto userDto = userService.findById(deltaDto.getId());

        if (userDto == null) {
            return;
        }

        userDto.update(
            deltaDto.getName(),
            deltaDto.getEmployeeNumber(),
            deltaDto.getLoginId(),
            deltaDto.getLocale(),
            deltaDto.getStatus(),
            deltaDto.getNeedOperatorAssignment(),
            deltaDto.getDirectTel(),
            deltaDto.getMobileNo(),
            deltaDto.getRepTel(),
            deltaDto.getFax(),
            deltaDto.getSelfInfo(),
            deltaDto.getJob(),
            deltaDto.getLocation(),
            deltaDto.getHomePage(),
            deltaDto.getMessenger(),
            deltaDto.getBirthday(),
            deltaDto.getLunarCalendar(),
            deltaDto.getAnniversary(),
            deltaDto.getAddress(),
            deltaDto.getMemo(),
            deltaDto.getExternalEmail(),
            deltaDto.getJoinDate(),
            deltaDto.getRecognizedJoinDate(),
            deltaDto.getResidentRegistrationNumber(),
            deltaDto.getEmployeeType(),
            deltaDto.getPositionCodeId(),
            deltaDto.getGradeCodeId()
        );
        userService.update(userDto);

        if (deltaDto.getMultiLanguageMap() != null) {
            List<MultiLanguageDto> multiLanguageDtos = getMultiLanguages(deltaDto.getId(), TargetDomain.USER,
                deltaDto.getMultiLanguageMap());
            List<MultiLanguageDto> multiLanguageDtosWithValue = multiLanguageDtos.stream()
                .filter(dto -> !dto.getValue().isEmpty())
                .toList();

            List<MultiLanguageDto> multiLanguageDtosWithoutValue = multiLanguageDtos.stream()
                .filter(dto -> dto.getValue().isEmpty())
                .toList();


            if (!multiLanguageDtosWithValue.isEmpty()) {
                multiLanguageService.update(multiLanguageDtosWithValue);
            }

            if (!multiLanguageDtosWithoutValue.isEmpty()) {
                multiLanguageService.delete(multiLanguageDtosWithoutValue);
            }
        }

        if (deltaDto.getUserGroupUserList() != null) {
            List<Long> userGroupCodeIds = getList(deltaDto.getUserGroupUserList());
            userGroupCodeUserService.deleteByUserId(deltaDto.getId());
            for (Long userGroupCodeId : userGroupCodeIds) {
                userGroupCodeUserService.create(new UserGroupUserDto(userDto.getId(), userGroupCodeId));
            }
        }
        info(companyDto.getUuid(), "update user. id: " + deltaDto.getId());
    }

    private void updateDepartment(DepartmentDeltaDto deltaDto, CompanyDto companyDto) {
        DepartmentDto departmentDto = departmentService.findById(deltaDto.getId());

        if (departmentDto == null) {
            return;
        }

        departmentDto.update(
            deltaDto.getName(),
            deltaDto.getParentId(),
            deltaDto.getSortOrder(),
            deltaDto.getCode(),
            deltaDto.getAlias(),
            deltaDto.getEmailId(),
            deltaDto.getStatus(),
            deltaDto.getDepartmentPath()
        );
        departmentService.update(departmentDto);

        if (deltaDto.getMultiLanguageDtoMap() != null) {
            List<MultiLanguageDto> multiLanguageDtos = getMultiLanguages(deltaDto.getId(), TargetDomain.USER,
                deltaDto.getMultiLanguageDtoMap());
            List<MultiLanguageDto> multiLanguageDtosWithValue = multiLanguageDtos.stream()
                .filter(dto -> !dto.getValue().isEmpty())
                .toList();

            List<MultiLanguageDto> multiLanguageDtosWithoutValue = multiLanguageDtos.stream()
                .filter(dto -> dto.getValue().isEmpty())
                .toList();


            if (!multiLanguageDtosWithValue.isEmpty()) {
                multiLanguageService.update(multiLanguageDtosWithValue);
            }

            if (!multiLanguageDtosWithoutValue.isEmpty()) {
                multiLanguageService.delete(multiLanguageDtosWithoutValue);
            }
        }
        info(companyDto.getUuid(), "update department. id: " + deltaDto.getId());
    }

    private void updateOrganization(OrganizationCodeDeltaDto deltaDto, CompanyDto companyDto) {
        OrganizationCodeDto organizationCodeDto = organizationCodeService.findById(deltaDto.getId());

        organizationCodeDto.update(
            deltaDto.getCode(),
            deltaDto.getType(),
            deltaDto.getName(),
            deltaDto.getSortOrder()
        );

        organizationCodeService.update(organizationCodeDto);

        if (deltaDto.getMultiLanguageDtoMap() != null) {
            List<MultiLanguageDto> multiLanguageDtos = getMultiLanguages(deltaDto.getId(), TargetDomain.USER,
                deltaDto.getMultiLanguageDtoMap());

            List<MultiLanguageDto> multiLanguageDtosWithValue = multiLanguageDtos.stream()
                .filter(dto -> !dto.getValue().isEmpty())
                .toList();

            List<MultiLanguageDto> multiLanguageDtosWithoutValue = multiLanguageDtos.stream()
                .filter(dto -> dto.getValue().isEmpty())
                .toList();


            if (!multiLanguageDtosWithValue.isEmpty()) {
                multiLanguageService.update(multiLanguageDtosWithValue);
            }

            if (!multiLanguageDtosWithoutValue.isEmpty()) {
                multiLanguageService.delete(multiLanguageDtosWithoutValue);
            }
        }

        info(companyDto.getUuid(), "update organization code. id: " + deltaDto.getId());
    }

    private void applyDelete(DomainType domainType, Long domainId, CompanyDto companyDto) {
        switch (domainType) {
            case ORGANIZATION_CODE -> {
                organizationCodeService.delete(domainId);
                multiLanguageService.delete(domainId, TargetDomain.ORGANIZATION_CODE);
                info(companyDto.getUuid(), "delete organization code. id: " + domainId);
            }
            case DEPARTMENT -> {
                departmentService.delete(domainId);
                multiLanguageService.delete(domainId, TargetDomain.DEPARTMENT);
                info(companyDto.getUuid(), "delete department. id: " + domainId);
            }
            case USER -> {
                userService.delete(domainId);
                multiLanguageService.delete(domainId, TargetDomain.USER);
                info(companyDto.getUuid(), "delete user. id: " + domainId);
            }
            case RELATION_MEMBER -> {
                memberService.delete(domainId);
                info(companyDto.getUuid(), "delete member. id: " + domainId);
            }
            default -> throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "not support domain type in applyDelete");
        }
    }

    private <T> List<T> getList(String list) {
        try {
            if (list == null || list.isBlank()) {
                return List.of();
            }
            return objectMapper.readValue(list, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "Error parsing integration updated value", e);
        }
    }

    private List<MultiLanguageDto> getMultiLanguages(Long domainId, TargetDomain targetDomain, String multiLanguageMap) {
        if (multiLanguageMap == null || multiLanguageMap.isBlank()) {
            return List.of();
        }
        List<MultiLanguageDto> multiLanguageDtos = new ArrayList<>();
        try {
            Map<String, String> multiLangauges = objectMapper.readValue(multiLanguageMap, new TypeReference<>() {});
            for (Entry<String, String> entry : multiLangauges.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null) {
                    continue;
                }
                MultiLanguageType multiLanguageType = MultiLanguageType.valueOf(key);
                multiLanguageDtos.add(new MultiLanguageDto(domainId, targetDomain, multiLanguageType, value));
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "error parsing json in MultiLanguageUtils", e);
        }
        return multiLanguageDtos;
    }

    private void info(String companyUuid, String message) {
        logger.info(Constants.DELTA_LOG_PREFIX + companyUuid + ", message: " + message);
    }
}
