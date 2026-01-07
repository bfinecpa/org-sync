package org.orgsync.core.engine;

import java.util.Map;
import org.orgsync.core.dto.DomainType;
import org.orgsync.core.dto.LogInfoDto;

/**
 * Callback invoked after delta operations are applied via JDBC.
 */
public interface SyncDeltaCallback {

    static SyncDeltaCallback noop() {
        return new SyncDeltaCallback() {
        };
    }

    default void afterCreate(String companyUuid,
                             DomainType domainType,
                             Long domainId,
                             Map<String, Object> columnValues,
                             Object dto) {
    }

    default void afterUpdate(String companyUuid,
                             LogInfoDto logInfoDto,
                             Object updatedValue) {
    }

    default void afterDelete(String companyUuid,
                             DomainType domainType,
                             Long domainId) {
    }
}
