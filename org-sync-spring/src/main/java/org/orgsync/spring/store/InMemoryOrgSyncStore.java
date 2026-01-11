package org.orgsync.spring.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.orgsync.core.dto.domainDto.CompanyDto;
import org.orgsync.core.dto.domainDto.CompanyGroupDto;
import org.orgsync.core.dto.domainDto.DepartmentDto;
import org.orgsync.core.dto.domainDto.IntegrationDto;
import org.orgsync.core.dto.domainDto.MemberDto;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.domainDto.OrganizationCodeDto;
import org.orgsync.core.dto.domainDto.UserDto;
import org.orgsync.core.dto.domainDto.UserGroupUserDto;
import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.type.TargetDomain;
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

public class InMemoryOrgSyncStore {

    private final AtomicLong companyIdSequence = new AtomicLong(1);
    private final Map<String, CompanyDto> companiesByUuid = new ConcurrentHashMap<>();
    private final Map<Long, CompanyDto> companiesById = new ConcurrentHashMap<>();
    private final Map<Long, CompanyGroupDto> companyGroups = new ConcurrentHashMap<>();
    private final Map<Long, IntegrationDto> integrations = new ConcurrentHashMap<>();
    private final Map<Long, OrganizationCodeDto> organizationCodes = new ConcurrentHashMap<>();
    private final Map<Long, DepartmentDto> departments = new ConcurrentHashMap<>();
    private final Map<Long, UserDto> users = new ConcurrentHashMap<>();
    private final Map<Long, MemberDto> members = new ConcurrentHashMap<>();
    private final Map<Long, Set<UserGroupUserDto>> userGroupUsersByUserId = new ConcurrentHashMap<>();
    private final Map<String, Long> logSeqByCompanyUuid = new ConcurrentHashMap<>();
    private final Map<Long, Long> logSeqByCompanyId = new ConcurrentHashMap<>();
    private final Map<MultiLanguageKey, Map<MultiLanguageType, MultiLanguageDto>> multiLanguageStore =
        new ConcurrentHashMap<>();

    private final OrgSyncLogSeqService logSeqService = new LogSeqService();
    private final OrgSyncCompanyService companyService = new CompanyService();
    private final OrgSyncCompanyGroupService companyGroupService = new CompanyGroupService();
    private final OrgSyncIntegrationService integrationService = new IntegrationService();
    private final OrgSyncOrganizationCodeService organizationCodeService = new OrganizationCodeService();
    private final OrgSyncDepartmentService departmentService = new DepartmentService();
    private final OrgSyncUserService userService = new UserService();
    private final OrgSyncMemberService memberService = new MemberService();
    private final OrgSyncUserGroupCodeUserService userGroupCodeUserService = new UserGroupCodeUserService();
    private final OrgSyncMultiLanguageService multiLanguageService = new MultiLanguageService();

    public CompanyDto registerCompany(String companyUuid) {
        return ensureCompany(companyUuid);
    }

    public OrgSyncLogSeqService logSeqService() {
        return logSeqService;
    }

    public OrgSyncCompanyService companyService() {
        return companyService;
    }

    public OrgSyncCompanyGroupService companyGroupService() {
        return companyGroupService;
    }

    public OrgSyncIntegrationService integrationService() {
        return integrationService;
    }

    public OrgSyncOrganizationCodeService organizationCodeService() {
        return organizationCodeService;
    }

    public OrgSyncDepartmentService departmentService() {
        return departmentService;
    }

    public OrgSyncUserService userService() {
        return userService;
    }

    public OrgSyncMemberService memberService() {
        return memberService;
    }

    public OrgSyncUserGroupCodeUserService userGroupCodeUserService() {
        return userGroupCodeUserService;
    }

    public OrgSyncMultiLanguageService multiLanguageService() {
        return multiLanguageService;
    }

    private CompanyDto ensureCompany(String companyUuid) {
        return companiesByUuid.computeIfAbsent(companyUuid, uuid -> {
            long id = companyIdSequence.getAndIncrement();
            CompanyDto companyDto = new CompanyDto(id, uuid, null);
            companiesById.put(id, companyDto);
            return companyDto;
        });
    }

    private boolean belongsToCompany(Long companyId, MemberDto dto) {
        if (dto.getUserId() != null) {
            UserDto userDto = users.get(dto.getUserId());
            if (userDto != null) {
                return companyId.equals(userDto.getCompanyId());
            }
        }
        if (dto.getDepartmentId() != null) {
            DepartmentDto departmentDto = departments.get(dto.getDepartmentId());
            if (departmentDto != null) {
                return companyId.equals(departmentDto.getCompanyId());
            }
        }
        return false;
    }

    private void upsertMultiLanguage(MultiLanguageDto dto) {
        if (dto == null || dto.getMultiLanguageType() == null) {
            return;
        }
        MultiLanguageKey key = new MultiLanguageKey(dto.getId(), dto.getTargetDomain());
        multiLanguageStore.computeIfAbsent(key, ignore -> new ConcurrentHashMap<>())
            .put(dto.getMultiLanguageType(), dto);
    }

    private record MultiLanguageKey(Long id, TargetDomain targetDomain) {
    }

    private class LogSeqService implements OrgSyncLogSeqService {

        @Override
        public Optional<Long> getLogSeq(String companyUuid) {
            return Optional.ofNullable(logSeqByCompanyUuid.get(companyUuid));
        }

        @Override
        public void saveLogSeq(Long companyId, Long logSeq) {
            logSeqByCompanyId.put(companyId, logSeq);
            CompanyDto companyDto = companiesById.get(companyId);
            if (companyDto != null && companyDto.getUuid() != null) {
                logSeqByCompanyUuid.put(companyDto.getUuid(), logSeq);
            }
        }
    }

    private class CompanyService implements OrgSyncCompanyService {

        @Override
        public CompanyDto findByUuid(String companyUuid) {
            return ensureCompany(companyUuid);
        }

        @Override
        public void updateCompanyGroupId(CompanyDto companyDto) {
            if (companyDto == null) {
                return;
            }
            companiesByUuid.put(companyDto.getUuid(), companyDto);
            companiesById.put(companyDto.getId(), companyDto);
        }

        @Override
        public boolean existsByCompanyGroupId(Long companyGroupId) {
            if (companyGroupId == null) {
                return false;
            }
            return companiesById.values().stream()
                .anyMatch(company -> companyGroupId.equals(company.getCompanyGroupId()));
        }
    }

    private class CompanyGroupService implements OrgSyncCompanyGroupService {

        @Override
        public CompanyGroupDto findById(Long id) {
            return companyGroups.get(id);
        }

        @Override
        public void create(CompanyGroupDto companyGroupDto) {
            if (companyGroupDto == null) {
                return;
            }
            companyGroups.put(companyGroupDto.getId(), companyGroupDto);
        }

        @Override
        public void update(CompanyGroupDto companyGroupDto) {
            create(companyGroupDto);
        }

        @Override
        public void delete(Long companyGroupId) {
            companyGroups.remove(companyGroupId);
        }

        @Override
        public CompanyGroupDto findByCompanyId(Long id) {
            CompanyDto companyDto = companiesById.get(id);
            if (companyDto == null || companyDto.getCompanyGroupId() == null) {
                return null;
            }
            return companyGroups.get(companyDto.getCompanyGroupId());
        }
    }

    private class IntegrationService implements OrgSyncIntegrationService {

        @Override
        public IntegrationDto findById(Long id) {
            return integrations.get(id);
        }

        @Override
        public void create(IntegrationDto integrationDto) {
            if (integrationDto == null) {
                return;
            }
            integrations.put(integrationDto.getId(), integrationDto);
        }

        @Override
        public void update(IntegrationDto integrationDto) {
            create(integrationDto);
        }

        @Override
        public void delete(Long id) {
            integrations.remove(id);
        }

        @Override
        public List<IntegrationDto> findByCompanyId(Long id) {
            return new ArrayList<>(integrations.values());
        }
    }

    private class OrganizationCodeService implements OrgSyncOrganizationCodeService {

        @Override
        public OrganizationCodeDto findById(Long id) {
            return organizationCodes.get(id);
        }

        @Override
        public void create(OrganizationCodeDto organizationCodeDto) {
            if (organizationCodeDto == null) {
                return;
            }
            organizationCodes.put(organizationCodeDto.getId(), organizationCodeDto);
        }

        @Override
        public void update(OrganizationCodeDto organizationCodeDto) {
            create(organizationCodeDto);
        }

        @Override
        public void delete(Long id) {
            organizationCodes.remove(id);
        }

        @Override
        public List<OrganizationCodeDto> findByCompanyId(Long id) {
            return organizationCodes.values().stream()
                .filter(dto -> id.equals(dto.getCompanyId()))
                .collect(Collectors.toList());
        }
    }

    private class DepartmentService implements OrgSyncDepartmentService {

        @Override
        public DepartmentDto findById(Long id) {
            return departments.get(id);
        }

        @Override
        public void create(DepartmentDto departmentDto) {
            if (departmentDto == null) {
                return;
            }
            departments.put(departmentDto.getId(), departmentDto);
        }

        @Override
        public void update(DepartmentDto departmentDto) {
            create(departmentDto);
        }

        @Override
        public void delete(Long departmentId) {
            departments.remove(departmentId);
        }

        @Override
        public List<DepartmentDto> findByCompanyId(Long id) {
            return departments.values().stream()
                .filter(dto -> id.equals(dto.getCompanyId()))
                .collect(Collectors.toList());
        }

        @Override
        public void updateParentId(DepartmentDto departmentDto) {
            if (departmentDto == null) {
                return;
            }
            DepartmentDto stored = departments.get(departmentDto.getId());
            if (stored != null) {
                stored.updateParentId(departmentDto.getParentId());
            }
        }
    }

    private class UserService implements OrgSyncUserService {

        @Override
        public UserDto findById(Long id) {
            return users.get(id);
        }

        @Override
        public void create(UserDto userDto) {
            if (userDto == null) {
                return;
            }
            users.put(userDto.getId(), userDto);
        }

        @Override
        public void update(UserDto userDto) {
            create(userDto);
        }

        @Override
        public void delete(Long userId) {
            users.remove(userId);
            userGroupUsersByUserId.remove(userId);
        }

        @Override
        public List<UserDto> findByCompanyId(Long id) {
            return users.values().stream()
                .filter(dto -> id.equals(dto.getCompanyId()))
                .collect(Collectors.toList());
        }

        @Override
        public UserDto findByCompanyIdAndIntegrationId(Long companyId, Long integrationId) {
            return users.values().stream()
                .filter(dto -> companyId.equals(dto.getCompanyId()))
                .filter(dto -> integrationId.equals(dto.getIntegrationId()))
                .findFirst()
                .orElse(null);
        }

        @Override
        public UserDto findByCompanyIdAndUserIds(Long id, List<Long> ids) {
            if (ids == null || ids.isEmpty()) {
                return null;
            }
            return users.values().stream()
                .filter(dto -> id.equals(dto.getCompanyId()))
                .filter(dto -> ids.contains(dto.getId()))
                .findFirst()
                .orElse(null);
        }

        @Override
        public void updateIntegration(UserDto userDto) {
            if (userDto == null) {
                return;
            }
            users.put(userDto.getId(), userDto);
        }

        @Override
        public boolean existsByIntegrationId(Long id) {
            return users.values().stream()
                .anyMatch(dto -> id.equals(dto.getIntegrationId()));
        }
    }

    private class MemberService implements OrgSyncMemberService {

        @Override
        public MemberDto findById(Long id) {
            return members.get(id);
        }

        @Override
        public void create(MemberDto memberDto) {
            if (memberDto == null) {
                return;
            }
            members.put(memberDto.getId(), memberDto);
        }

        @Override
        public void update(MemberDto memberDto) {
            create(memberDto);
        }

        @Override
        public void delete(Long memberId) {
            members.remove(memberId);
        }

        @Override
        public List<MemberDto> findByCompanyId(Long id) {
            return members.values().stream()
                .filter(dto -> belongsToCompany(id, dto))
                .collect(Collectors.toList());
        }
    }

    private class UserGroupCodeUserService implements OrgSyncUserGroupCodeUserService {

        @Override
        public void create(UserGroupUserDto userGroupUserDto) {
            if (userGroupUserDto == null) {
                return;
            }
            userGroupUsersByUserId.computeIfAbsent(userGroupUserDto.getUserId(), key -> ConcurrentHashMap.newKeySet())
                .add(userGroupUserDto);
        }

        @Override
        public void deleteByUserId(Long id) {
            userGroupUsersByUserId.remove(id);
        }
    }

    private class MultiLanguageService implements OrgSyncMultiLanguageService {

        @Override
        public void create(List<MultiLanguageDto> multiLanguageDtos) {
            if (multiLanguageDtos == null) {
                return;
            }
            for (MultiLanguageDto dto : multiLanguageDtos) {
                upsertMultiLanguage(dto);
            }
        }

        @Override
        public void delete(Long id, TargetDomain targetDomain) {
            multiLanguageStore.remove(new MultiLanguageKey(id, targetDomain));
        }

        @Override
        public void delete(List<MultiLanguageDto> multiLanguageDtos) {
            if (multiLanguageDtos == null) {
                return;
            }
            for (MultiLanguageDto dto : multiLanguageDtos) {
                MultiLanguageKey key = new MultiLanguageKey(dto.getId(), dto.getTargetDomain());
                Map<MultiLanguageType, MultiLanguageDto> entries = multiLanguageStore.get(key);
                if (entries != null) {
                    entries.remove(dto.getMultiLanguageType());
                }
            }
        }

        @Override
        public void update(List<MultiLanguageDto> multiLanguageDto) {
            create(multiLanguageDto);
        }
    }
}
