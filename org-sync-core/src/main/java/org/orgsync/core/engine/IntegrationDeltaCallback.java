package org.orgsync.core.engine;

import java.util.Map;
import org.orgsync.core.dto.IntegrationDto;
import org.orgsync.core.dto.LogInfoDto;

/**
 * Callback invoked after integration delta operations are applied.
 */
public interface IntegrationDeltaCallback {

    static IntegrationDeltaCallback noop() {
        return new IntegrationDeltaCallback() {
        };
    }

    default void afterCreate(String companyUuid,
                             Long integrationId,
                             Map<String, Object> columnValues,
                             IntegrationDto dto) {
    }

    default void afterUpdate(String companyUuid,
                             LogInfoDto logInfoDto,
                             Object updatedValue) {
    }

    default void afterDelete(String companyUuid,
                             Long integrationId) {
    }
}
