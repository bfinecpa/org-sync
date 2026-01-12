package org.orgsync.springsample.store.jpa.service;

import java.util.List;
import java.util.stream.Collectors;
import org.orgsync.springsample.store.jpa.entity.UserEntity;
import org.orgsync.springsample.store.jpa.repository.UserGroupUserRepository;
import org.orgsync.springsample.store.jpa.repository.UserRepository;
import org.orgsync.core.dto.domainDto.UserDto;
import org.orgsync.core.service.OrgSyncUserService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncUserService implements OrgSyncUserService {

    private final UserRepository userRepository;
    private final UserGroupUserRepository userGroupUserRepository;

    public JpaOrgSyncUserService(UserRepository userRepository, UserGroupUserRepository userGroupUserRepository) {
        this.userRepository = userRepository;
        this.userGroupUserRepository = userGroupUserRepository;
    }

    @Override
    public UserDto findById(Long id) {
        return userRepository.findById(id)
            .map(this::toDto)
            .orElse(null);
    }

    @Override
    public void create(UserDto userDto) {
        if (userDto == null) {
            return;
        }
        userRepository.save(toEntity(userDto));
    }

    @Override
    public void update(UserDto userDto) {
        create(userDto);
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
        userGroupUserRepository.deleteByIdUserId(userId);
    }

    @Override
    public List<UserDto> findByCompanyId(Long id) {
        return userRepository.findByCompanyId(id).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public UserDto findByCompanyIdAndIntegrationId(Long companyId, Long integrationId) {
        return userRepository.findByCompanyIdAndIntegrationId(companyId, integrationId)
            .map(this::toDto)
            .orElse(null);
    }

    @Override
    public UserDto findByCompanyIdAndUserIds(Long id, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return userRepository.findFirstByCompanyIdAndIdIn(id, ids)
            .map(this::toDto)
            .orElse(null);
    }

    @Override
    public void updateIntegration(UserDto userDto) {
        if (userDto == null) {
            return;
        }
        userRepository.findById(userDto.getId())
            .ifPresent(entity -> {
                entity.setIntegrationId(userDto.getIntegrationId());
                userRepository.save(entity);
            });
    }

    @Override
    public boolean existsByIntegrationId(Long id) {
        return userRepository.existsByIntegrationId(id);
    }

    private UserEntity toEntity(UserDto dto) {
        return new UserEntity(dto.getId(), dto.getCompanyId(), dto.getName(), dto.getEmployeeNumber(),
            dto.getLoginId(), dto.getLocale(), dto.getStatus(), dto.getNeedOperatorAssignment(),
            dto.getDirectTel(), dto.getMobileNo(), dto.getRepTel(), dto.getFax(), dto.getSelfInfo(),
            dto.getJob(), dto.getLocation(), dto.getHomePage(), dto.getMessenger(), dto.getBirthday(),
            dto.isLunarCalendar(), dto.getAnniversary(), dto.getAddress(), dto.getMemo(), dto.getExternalEmail(),
            dto.getJoinDate(), dto.getRecognizedJoinDate(), dto.getResidentRegistrationNumber(),
            dto.getEmployeeType(), dto.getPositionCodeId(), dto.getGradeCodeId(), dto.getIntegrationId());
    }

    private UserDto toDto(UserEntity entity) {
        UserDto dto = new UserDto(entity.getId(), entity.getCompanyId(), entity.getName(), entity.getEmployeeNumber(),
            entity.getLoginId(), entity.getLocale(), entity.getStatus(), entity.getNeedOperatorAssignment(),
            entity.getDirectTel(), entity.getMobileNo(), entity.getRepTel(), entity.getFax(), entity.getSelfInfo(),
            entity.getJob(), entity.getLocation(), entity.getHomePage(), entity.getMessenger(), entity.getBirthday(),
            entity.isLunarCalendar(), entity.getAnniversary(), entity.getAddress(), entity.getMemo(),
            entity.getExternalEmail(), entity.getJoinDate(), entity.getRecognizedJoinDate(),
            entity.getResidentRegistrationNumber(), entity.getEmployeeType(), entity.getPositionCodeId(),
            entity.getGradeCodeId());
        dto.updateIntegrationId(entity.getIntegrationId());
        return dto;
    }
}
