package org.orgsync.bootsample.store.jpa.service;

import java.util.List;
import org.orgsync.bootsample.store.jpa.entity.UserGroupUserEntity;
import org.orgsync.bootsample.store.jpa.entity.UserGroupUserId;
import org.orgsync.bootsample.store.jpa.repository.UserGroupUserRepository;
import org.orgsync.core.dto.domainDto.UserGroupUserDto;
import org.orgsync.core.service.OrgSyncUserGroupCodeUserService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncUserGroupCodeUserService implements OrgSyncUserGroupCodeUserService {

    private final UserGroupUserRepository userGroupUserRepository;

    public JpaOrgSyncUserGroupCodeUserService(UserGroupUserRepository userGroupUserRepository) {
        this.userGroupUserRepository = userGroupUserRepository;
    }

    @Override
    public void create(UserGroupUserDto userGroupUserDto) {
        if (userGroupUserDto == null) {
            return;
        }
        UserGroupUserId id = new UserGroupUserId(userGroupUserDto.getUserId(), userGroupUserDto.getUserGroupId());
        userGroupUserRepository.save(new UserGroupUserEntity(id));
    }

    @Override
    public void deleteByUserId(Long id) {
        userGroupUserRepository.deleteByIdUserId(id);
    }


    @Override
    public List<UserGroupUserDto> findByUserId(Long id) {
        return userGroupUserRepository.findByIdUserId(id).stream()
            .map(entity -> new UserGroupUserDto(entity.getId().getUserId(), entity.getId().getUserGroupId()))
            .toList();
    }

    @Override
    public void deleteByUserIdAndUserGroupCodeId(Long userId, Long userGroupCodeId) {
        userGroupUserRepository.deleteByIdUserIdAndIdUserGroupId(userId, userGroupCodeId);
    }
}
