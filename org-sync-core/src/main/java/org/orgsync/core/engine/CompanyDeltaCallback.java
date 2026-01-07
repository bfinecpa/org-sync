package org.orgsync.core.engine;

import java.util.Map;
import org.orgsync.core.dto.CompanyDto;
import org.orgsync.core.dto.LogInfoDto;

/**
 * Callback invoked after company delta operations are applied.
 */
public interface CompanyDeltaCallback {

    static CompanyDeltaCallback noop() {
        return new CompanyDeltaCallback() {
        };
    }

    default void afterCreate(String companyUuid,
                             Long companyId,
                             Map<String, Object> columnValues,
                             CompanyDto dto) {
    }

    default void afterUpdate(String companyUuid,
                             LogInfoDto logInfoDto,
                             Object updatedValue) {
    }

    default void afterDelete(String companyUuid,
                             Long companyId) {
    }
}
