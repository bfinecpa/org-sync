package org.orgsync.core.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.dto.ProvisionSequenceDto;
import org.orgsync.core.dto.domainDto.CompanyDto;
import org.orgsync.core.dto.domainDto.CompanyGroupDto;
import org.orgsync.core.dto.domainDto.DepartmentDto;
import org.orgsync.core.dto.domainDto.IntegrationDto;
import org.orgsync.core.dto.domainDto.MemberDto;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.domainDto.OrganizationCodeDto;
import org.orgsync.core.dto.domainDto.UserDto;
import org.orgsync.core.dto.domainDto.UserGroupUserDto;
import org.orgsync.core.dto.snapshotDto.CompanyGroupSnapshotDto;
import org.orgsync.core.dto.snapshotDto.DepartmentSnapshotDto;
import org.orgsync.core.dto.snapshotDto.IntegrationSnapshotDto;
import org.orgsync.core.dto.snapshotDto.OrganizationCodeSnapshotDto;
import org.orgsync.core.dto.snapshotDto.SnapshotDto;
import org.orgsync.core.dto.snapshotDto.TreeDepartmentNodeSnapshotDto;
import org.orgsync.core.dto.snapshotDto.TreeSnapshotDto;
import org.orgsync.core.dto.snapshotDto.UserGroupSnapshotDto;
import org.orgsync.core.dto.snapshotDto.UserSnapshotDto;
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
import org.orgsync.core.Constants;

class SyncSnapshotApplier {

    private final OrgChartClient client;
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
    private final SyncLogger logger;

    SyncSnapshotApplier(OrgChartClient client, OrgSyncLogSeqService logSeqService,
        OrgSyncOrganizationCodeService organizationCodeService, OrgSyncDepartmentService departmentService,
        OrgSyncUserService userService, OrgSyncMemberService memberService,
        OrgSyncIntegrationService integrationService, OrgSyncCompanyGroupService companyGroupService,
        OrgSyncCompanyService companyService, OrgSyncUserGroupCodeUserService userGroupCodeUserService,
        OrgSyncMultiLanguageService multiLanguageService, SyncLogger logger) {
        this.client = client;
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
        this.logger = logger;
    }

    long applySnapshot(String companyUuid, ProvisionSequenceDto sequenceDto) {
        CompanyDto companyDto = companyService.findByUuid(companyUuid);
        Long lastLogSeq = sequenceDto.logSeq();

        for (Long snapshotId : sequenceDto.snapshotIdList()) {
            info(companyUuid, "Apply snapshot. snapshotId=" + snapshotId);
            SnapshotDto snapshotDto = client.fetchSnapshot(companyUuid, snapshotId);
            compareCompanyGroup(snapshotDto.companyGroupSnapshot(), companyDto);
            compareIntegration(snapshotDto.integrationSnapshot(), companyDto);
            compareOrganizationCode(snapshotDto.organizationCodeSnapshot(), companyDto);
            compareUser(snapshotDto.userSnapshot(), companyDto);
            compareDepartment(snapshotDto.departmentSnapshot(), companyDto);
            compareDepartmentMember(snapshotDto.relationSnapshot(), companyDto);
            logSeqService.saveLogSeq(companyDto.getId(), snapshotDto.logSeq());
            lastLogSeq = snapshotDto.logSeq();
            info(companyUuid, "Snapshot applied. snapshotId=" + snapshotId + ", lastLogSeq=" + lastLogSeq);
        }
        return lastLogSeq;
    }

    private void compareDepartmentMember(List<TreeSnapshotDto> relationSnapshot, CompanyDto companyDto) {
        List<MemberDto> memberDtos = memberService.findByCompanyId(companyDto.getId());

        if (memberDtos == null) {
            return;
        }

        List<MemberDto> snapshotMemberDtos = relationSnapshot.stream().flatMap(TreeSnapshotDto::toMemberDto).toList();

        List<Long> newIds = snapshotMemberDtos.stream().map(MemberDto::getId).toList();
        List<Long> oldIds = memberDtos.stream().map(MemberDto::getId).toList();

        for (MemberDto snapshotMemberDto : snapshotMemberDtos) {
            if (!oldIds.contains(snapshotMemberDto.getId())) {
                memberService.create(snapshotMemberDto);
                info(companyDto.getUuid(), "create member. id: " + snapshotMemberDto.getId());
            } else {
                memberService.update(snapshotMemberDto);
                info(companyDto.getUuid(), "updated member. id: " + snapshotMemberDto.getId());
            }
        }
        for (MemberDto memberDto : memberDtos) {
            if (!newIds.contains(memberDto.getId())) {
                memberService.delete(memberDto.getId());
                info(companyDto.getUuid(), "delete member. id: " + memberDto.getId());
            }
        }

        // 부모 부서 확인
        List<DepartmentDto> departmentDtos = departmentService.findByCompanyId(companyDto.getId());
        if (departmentDtos == null) {
            return;
        }

        Map<Long, DepartmentDto> dtoMap = departmentDtos.stream()
            .collect(Collectors.toMap(dto -> dto.getId(), dto -> dto));


        for (TreeSnapshotDto treeSnapshotDto : relationSnapshot) {
            if (treeSnapshotDto.isDeleted()) {
                continue;
            }

            Long parentId = treeSnapshotDto.id();

            for (TreeDepartmentNodeSnapshotDto childDepartment : treeSnapshotDto.childDepartments()) {
                if (childDepartment.isDeleted()) {
                    continue;
                }

                DepartmentDto departmentDto = dtoMap.get(childDepartment.id());
                if (departmentDto == null) {
                    throw new IllegalStateException("No such department " + childDepartment.id());
                }

                departmentDto.updateParentId(parentId);
                departmentService.updateParentId(departmentDto);
                info(companyDto.getUuid(), "update department. id: " + departmentDto.getId() + " parentId: " + parentId);
            }
        }
    }

    private void compareDepartment(List<DepartmentSnapshotDto> departmentSnapshot, CompanyDto companyDto) {
        List<DepartmentDto> departmentDtos = departmentService.findByCompanyId(companyDto.getId());

        if (departmentDtos == null) {
            return;
        }

        Set<Long> newIds = departmentSnapshot.stream().map(DepartmentSnapshotDto::deptId).collect(Collectors.toSet());
        Set<Long> oldIds = departmentDtos.stream().map(DepartmentDto::getId).collect(Collectors.toSet());


        for (DepartmentSnapshotDto departmentSnapshotDto : departmentSnapshot) {
            DepartmentDto departmentDto = departmentSnapshotDto.toDepartmentDto(companyDto.getId());
            List<MultiLanguageDto> multiLanguageDtos = departmentSnapshotDto.toMultiLanguageDtos();
            List<MultiLanguageDto> multiLanguageDtosWithValue = multiLanguageDtos.stream()
                .filter(dto -> !dto.getValue().isEmpty())
                .toList();

            List<MultiLanguageDto> multiLanguageDtosWithoutValue = multiLanguageDtos.stream()
                .filter(dto -> dto.getValue().isEmpty())
                .toList();


            if (!oldIds.contains(departmentDto.getId())) {
                departmentService.create(departmentDto);
                multiLanguageService.create(multiLanguageDtosWithValue);
                info(companyDto.getUuid(), "create department. id: " + departmentDto.getId());
            } else {
                departmentService.update(departmentDto);
                multiLanguageService.update(multiLanguageDtosWithValue);
                multiLanguageService.delete(multiLanguageDtosWithoutValue);
                info(companyDto.getUuid(), "update department. id: " + departmentDto.getId());
            }
        }

        for (DepartmentDto departmentDto : departmentDtos) {
            if (!newIds.contains(departmentDto.getId())) {
                departmentService.delete(departmentDto.getId());
                multiLanguageService.delete(departmentDto.getId(), TargetDomain.DEPARTMENT);
                info(companyDto.getUuid(), "delete department. id: " + departmentDto.getId());
            }
        }
    }

    private void compareUser(List<UserSnapshotDto> userSnapshot, CompanyDto companyDto) {
        List<UserDto> userDtos = userService.findByCompanyId(companyDto.getId());

        if (userDtos == null) {
            return;
        }

        Set<Long> newIds = userSnapshot.stream().map(UserSnapshotDto::userId).collect(Collectors.toSet());
        Set<Long> oldIds = userDtos.stream().map(UserDto::getId).collect(Collectors.toSet());

        for (UserSnapshotDto userSnapshotDto : userSnapshot) {
            UserDto userDto = userSnapshotDto.toUserDto(companyDto.getId());
            List<MultiLanguageDto> multiLanguageDtos = userSnapshotDto.toMultiLanguageDtos();
            List<MultiLanguageDto> multiLanguageDtosWithValue = multiLanguageDtos.stream()
                .filter(dto -> !dto.getValue().isEmpty())
                .toList();
            List<MultiLanguageDto> multiLanguageDtosWithoutValue = multiLanguageDtos.stream()
                .filter(dto -> dto.getValue().isEmpty())
                .toList();

            if (!oldIds.contains(userSnapshotDto.userId())) {
                // 생성
                userService.create(userDto);
                multiLanguageService.create(multiLanguageDtosWithValue);
                for (UserGroupSnapshotDto userGroupSnapshotDto : userSnapshotDto.userGroupList()) {
                    UserGroupUserDto userGroupUserDto = new UserGroupUserDto(userSnapshotDto.userId(),
                        userGroupSnapshotDto.id());
                    userGroupCodeUserService.create(userGroupUserDto);
                }
                info(companyDto.getUuid(), "create user. id: " + userDto.getId());
            } else {
                // 업데이트
                userService.update(userDto);
                multiLanguageService.update(multiLanguageDtosWithValue);
                multiLanguageService.delete(multiLanguageDtosWithoutValue);

                Set<Long> oldUserGroupCodeIds = userGroupCodeUserService.findByUserId(userDto.getId())
                    .stream()
                    .map(UserGroupUserDto::getUserGroupId)
                    .collect(Collectors.toSet());

                Set<Long> newUserGroupCodeIds = userSnapshotDto.userGroupList()
                    .stream()
                    .map(UserGroupSnapshotDto::id)
                    .collect(Collectors.toSet());

                for (Long newUserGroupCodeId : newUserGroupCodeIds) {
                    if (!oldUserGroupCodeIds.contains(newUserGroupCodeId)) {
                        userGroupCodeUserService.create(new UserGroupUserDto(userDto.getId(), newUserGroupCodeId));
                    }
                }

                for (Long oldUserGroupCodeId : oldUserGroupCodeIds) {
                    if (!newUserGroupCodeIds.contains(oldUserGroupCodeId)) {
                        userGroupCodeUserService.deleteByUserIdAndUserGroupCodeId(userDto.getId(), oldUserGroupCodeId);
                    }
                }

                info(companyDto.getUuid(), "update user. id: " + userDto.getId());
            }
        }

        for (UserDto userDto : userDtos) {
            if (!newIds.contains(userDto.getId())) {
                //삭제
                userService.delete(userDto.getId());
                multiLanguageService.delete(userDto.getId(), TargetDomain.USER);
                userGroupCodeUserService.deleteByUserId(userDto.getId());
                info(companyDto.getUuid(), "delete user. id: " + userDto.getId());
            }
        }
    }

    private void compareOrganizationCode(List<OrganizationCodeSnapshotDto> organizationCodeSnapshot, CompanyDto companyDto) {
        List<OrganizationCodeDto> organizationCodeDtos = organizationCodeService.findByCompanyId(companyDto.getId());

        if (organizationCodeDtos == null) {
            return;
        }

        Set<Long> newIds = organizationCodeSnapshot.stream().map(OrganizationCodeSnapshotDto::id)
            .collect(Collectors.toSet());
        Set<Long> oldIds = organizationCodeDtos.stream().map(OrganizationCodeDto::getId).collect(Collectors.toSet());

        for (OrganizationCodeSnapshotDto organizationCodeSnapshotDto : organizationCodeSnapshot) {
            // 생성
            OrganizationCodeDto organizationCodeDto = organizationCodeSnapshotDto.toOrganizationCodeDto(
                companyDto.getId());
            List<MultiLanguageDto> multiLanguageDtos = organizationCodeSnapshotDto.toMultiLanguageDtos();
            List<MultiLanguageDto> multiLanguageDtosWithValue = multiLanguageDtos.stream()
                .filter(dto -> !dto.getValue().isEmpty())
                .toList();

            List<MultiLanguageDto> multiLanguageDtosWithoutValue = multiLanguageDtos.stream()
                .filter(dto -> dto.getValue().isEmpty())
                .toList();

            if (!oldIds.contains(organizationCodeSnapshotDto.id())) {
                organizationCodeService.create(organizationCodeDto);
                multiLanguageService.create(multiLanguageDtosWithValue);
                info(companyDto.getUuid(), "create organization. id: " + organizationCodeDto.getId());

            } else {
                // 업데이트
                organizationCodeService.update(organizationCodeDto);
                multiLanguageService.update(multiLanguageDtosWithValue);
                multiLanguageService.delete(multiLanguageDtosWithoutValue);
                info(companyDto.getUuid(), "update organization. id: " + organizationCodeDto.getId());
            }
        }

        for (OrganizationCodeDto organizationCodeDto : organizationCodeDtos) {
            if (!newIds.contains(organizationCodeDto.getId())) {
                // 삭제
                organizationCodeService.delete(organizationCodeDto.getId());
                multiLanguageService.delete(organizationCodeDto.getId(), TargetDomain.DEPARTMENT);
                info(companyDto.getUuid(), "delete organization. id: " + organizationCodeDto.getId());
            }
        }
    }

    private void compareIntegration(List<IntegrationSnapshotDto> integrationSnapshot, CompanyDto companyDto) {
        List<IntegrationDto> integrationDtos = integrationService.findByCompanyId(companyDto.getId());

        if (integrationDtos == null) {
            return;
        }

        Set<Long> newIds = integrationSnapshot.stream().map(IntegrationSnapshotDto::id).collect(Collectors.toSet());
        Set<Long> oldIds = integrationDtos.stream().map(IntegrationDto::getId).collect(Collectors.toSet());

        for (IntegrationSnapshotDto integrationSnapshotDto : integrationSnapshot) {
            if (!oldIds.contains(integrationSnapshotDto.id())) {
                IntegrationDto integrationDto = new IntegrationDto(integrationSnapshotDto.id());
                integrationService.create(integrationDto);
                info(companyDto.getUuid(), "create integration. id: " + integrationDto.getId());
                UserDto userDto = userService.findByCompanyIdAndUserIds(companyDto.getId(),
                    integrationSnapshotDto.userIdList());

                if (userDto == null) {
                    continue;
                }
                userDto.updateIntegrationId(integrationDto.getId());
                userService.updateIntegration(userDto);
                info(companyDto.getUuid(), "update integration. user id: " + userDto.getId() + " integration id: "
                    + integrationDto.getId());
            }
        }

        for (IntegrationDto integrationDto : integrationDtos) {
            if (!newIds.contains(integrationDto.getId())) {

                UserDto userDto = userService.findByCompanyIdAndIntegrationId(companyDto.getId(),
                    integrationDto.getId());
                if (userDto == null) {
                    continue;
                }
                userDto.updateIntegrationId(null);
                userService.updateIntegration(userDto);
                info(companyDto.getUuid(), "update integration. user id: " + userDto.getId() + " integration id: null");

                if (userService.existsByIntegrationId(integrationDto.getId())) {
                    integrationService.delete(integrationDto.getId());
                    info(companyDto.getUuid(), "delete integration. id: " + integrationDto.getId());
                }
            }
        }
    }

    private void compareCompanyGroup(List<CompanyGroupSnapshotDto> companyGroupSnapshot, CompanyDto companyDto) {
        if (companyGroupSnapshot != null && !companyGroupSnapshot.isEmpty()) {
            CompanyGroupSnapshotDto companyGroupSnapshotDto = companyGroupSnapshot.get(0);
            CompanyGroupDto companyGroupDto = companyGroupService.findById(companyGroupSnapshotDto.id());
            if (companyGroupDto == null) {
                companyGroupDto = new CompanyGroupDto(companyGroupSnapshotDto.id());
                companyGroupService.create(companyGroupDto);
                info(companyDto.getUuid(), "create company group. id: " + companyGroupDto.getId());
            }

            if (companyDto.getCompanyGroupId() == null) {
                companyDto.updateCompanyGroupId(companyGroupDto.getId());
                companyService.updateCompanyGroupId(companyDto);
                info(companyDto.getUuid(), "update company group. company id: " + companyDto.getId()
                    + " company group: " + companyGroupDto.getId());
            } else if (!companyDto.getCompanyGroupId().equals(companyGroupDto.getId())) {
                companyDto.updateCompanyGroupId(companyGroupDto.getId());
                companyService.updateCompanyGroupId(companyDto);
                info(companyDto.getUuid(), "update company group. company id: " + companyDto.getId()
                    + " company group: " + companyGroupDto.getId());
            }
        } else {
            if (companyDto.getCompanyGroupId() != null) {
                companyDto.updateCompanyGroupId(null);
                companyService.updateCompanyGroupId(companyDto);
                info(companyDto.getUuid(), "update company group. company id: " + companyDto.getId()
                    + " company group: null");
            }

            if (companyService.existsByCompanyGroupId(companyDto.getCompanyGroupId())) {
                companyGroupService.delete(companyDto.getCompanyGroupId());
                info(companyDto.getUuid(), "delete company group. id: " + companyDto.getCompanyGroupId());
            }
        }
    }

    private void info(String companyUuid, String message) {
        logger.info(Constants.ORG_SYNC_LOG_PREFIX + companyUuid + ", message: " + message);
    }
}
