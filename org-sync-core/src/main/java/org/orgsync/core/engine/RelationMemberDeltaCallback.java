package org.orgsync.core.engine;

import java.util.Map;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.MemberDto;

/**
 * Callback invoked after relation member delta operations are applied.
 */
public interface RelationMemberDeltaCallback {

    static RelationMemberDeltaCallback noop() {
        return new RelationMemberDeltaCallback() {
        };
    }

    default void afterCreate(String companyUuid,
                             Long memberId,
                             Map<String, Object> columnValues,
                             MemberDto dto) {
    }

    default void afterUpdate(String companyUuid,
                             LogInfoDto logInfoDto,
                             Object updatedValue) {
    }

    default void afterDelete(String companyUuid,
                             Long memberId) {
    }
}
