package org.orgsync.core.engine;

import java.util.Map;
import org.orgsync.core.dto.CompanyGroupDto;
import org.orgsync.core.dto.LogInfoDto;

/**
 * Callback invoked after company group delta operations are applied.
 */
public interface CompanyGroupDeltaCallback {

    static CompanyGroupDeltaCallback noop() {
        return new CompanyGroupDeltaCallback() {
        };
    }

    default void afterCreate(String companyUuid,
                             Long companyGroupId,
                             Map<String, Object> columnValues,
                             CompanyGroupDto dto) {
    }

    default void afterUpdate(String companyUuid,
                             LogInfoDto logInfoDto,
                             Object updatedValue) {
    }

    default void afterDelete(String companyUuid,
                             Long companyGroupId) {
    }
}
