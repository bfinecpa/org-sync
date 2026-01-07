package org.orgsync.core.repository;

import org.orgsync.core.dto.UserDto;

public interface UserRepository {

    void create(String companyUuid, UserDto userDto);

    void update(String companyUuid, Long userId, String fieldName, Object updatedValue);

    void delete(String companyUuid, Long userId);
}
