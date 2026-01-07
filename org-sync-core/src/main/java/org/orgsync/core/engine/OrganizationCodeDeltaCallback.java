package org.orgsync.core.engine;

import java.util.Map;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.OrganizationCodeDto;

/**
 * Callback invoked after organization code delta operations are applied.
 */
public interface OrganizationCodeDeltaCallback {

    static OrganizationCodeDeltaCallback noop() {
        return new OrganizationCodeDeltaCallback() {
        };
    }

    default void afterCreate(String companyUuid,
                             Long organizationCodeId,
                             Map<String, Object> columnValues,
                             OrganizationCodeDto dto) {
    }

    default void afterUpdate(String companyUuid,
                             LogInfoDto logInfoDto,
                             Object updatedValue) {
    }

    default void afterDelete(String companyUuid,
                             Long organizationCodeId) {
    }
}
