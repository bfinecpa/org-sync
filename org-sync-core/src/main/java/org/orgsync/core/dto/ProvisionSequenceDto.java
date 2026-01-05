package org.orgsync.core.dto;

import java.util.List;

/**
 * Response payload of the provision sequence API.
 */
public record ProvisionSequenceDto(
        boolean needSnapshot,
        List<Long> snapshotIdList,
        Long logSeq,
        boolean needUpdateNextLog,
        List<LogInfoDto> logInfoList
) {
}
