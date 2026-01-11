package org.orgsync.core.service;

import org.orgsync.core.dto.domainDto.UserGroupUserDto;

public interface OrgSyncUserGroupUserService {


    default void create(UserGroupUserDto dto) {}

    default void update(UserGroupUserDto dto) {}

    default void delete(Long userId) {}

}
