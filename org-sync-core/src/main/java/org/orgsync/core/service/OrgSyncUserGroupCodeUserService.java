package org.orgsync.core.service;

import java.util.List;
import org.orgsync.core.dto.domainDto.UserGroupUserDto;

public interface OrgSyncUserGroupCodeUserService {

    default void create(UserGroupUserDto userGroupUserDto){}

    default void deleteByUserId(Long id){}

    default List<UserGroupUserDto> findByUserId(Long id){return List.of();};

    default void deleteByUserIdAndUserGroupCodeId(Long id, Long oldUserGroupCodeId) {}
}
