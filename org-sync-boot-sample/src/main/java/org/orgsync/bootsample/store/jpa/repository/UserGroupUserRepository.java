package org.orgsync.bootsample.store.jpa.repository;

import java.util.List;
import org.orgsync.bootsample.store.jpa.entity.UserGroupUserEntity;
import org.orgsync.bootsample.store.jpa.entity.UserGroupUserId;
import org.orgsync.core.dto.domainDto.UserGroupUserDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupUserRepository extends JpaRepository<UserGroupUserEntity, UserGroupUserId> {

    void deleteByIdUserId(Long userId);

    void deleteByIdUserIdAndIdUserGroupId(Long userId, Long userGroupCodeId);

    List<UserGroupUserEntity> findByIdUserId(Long id);
}
