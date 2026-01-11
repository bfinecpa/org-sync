package org.orgsync.core.service;

import org.orgsync.core.dto.domainDto.UserGroupUserDto;

public interface OrgSyncUserGroupCodeUserService {

    default void create(UserGroupUserDto userGroupUserDto){}

    default void deleteByUserId(Long id){}
}
