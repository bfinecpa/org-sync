package org.orgsync.core.engine;

import java.util.Map;
import org.orgsync.core.dto.DepartmentDto;
import org.orgsync.core.dto.LogInfoDto;

/**
 * Callback invoked after department delta operations are applied.
 */
public interface DepartmentDeltaCallback {

    static DepartmentDeltaCallback noop() {
        return new DepartmentDeltaCallback() {
        };
    }

    default void afterCreate(String companyUuid,
                             Long departmentId,
                             Map<String, Object> columnValues,
                             DepartmentDto dto) {
    }

    default void afterUpdate(String companyUuid,
                             LogInfoDto logInfoDto,
                             Object updatedValue) {
    }

    default void afterDelete(String companyUuid,
                             Long departmentId) {
    }
}
