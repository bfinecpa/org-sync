package org.orgsync.springsample.store.jpa.repository;

import org.orgsync.springsample.store.jpa.entity.UserGroupUserEntity;
import org.orgsync.springsample.store.jpa.entity.UserGroupUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupUserRepository extends JpaRepository<UserGroupUserEntity, UserGroupUserId> {

    void deleteByIdUserId(Long userId);
}
