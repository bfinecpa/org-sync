package org.orgsync.core.service;

import java.util.List;
import org.orgsync.core.dto.domainDto.UserDto;

public interface OrgSyncUserService {

    default UserDto findById(Long id) { return null; }

    default void create(UserDto userDto) {}

    default void update(UserDto userDto) {}

    default void delete(Long userId) {}

    default List<UserDto> findByCompanyId(Long id) { return null; }

    default UserDto findByCompanyIdAndIntegrationId(Long companyId, Long integrationId) { return null; }

    default UserDto findByCompanyIdAndUserIds(Long id, List<Long> ids) { return null; }

    default void updateIntegration(UserDto userDto) {}

    default boolean existsByIntegrationId(Long id) {return false;}
}
