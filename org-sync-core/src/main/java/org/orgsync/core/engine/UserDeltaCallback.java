package org.orgsync.core.engine;

import java.util.Map;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.UserDto;

/**
 * Callback invoked after user delta operations are applied.
 */
public interface UserDeltaCallback {

    static UserDeltaCallback noop() {
        return new UserDeltaCallback() {
        };
    }

    default void afterCreate(String companyUuid,
                             Long userId,
                             Map<String, Object> columnValues,
                             UserDto dto) {
    }

    default void afterUpdate(String companyUuid,
                             LogInfoDto logInfoDto,
                             Object updatedValue) {
    }

    default void afterDelete(String companyUuid,
                             Long userId) {
    }
}
