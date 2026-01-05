package org.orgsync.core.dto;

import java.util.List;

public record ProvisionSequenceDto(
    boolean needSnapshot,
    List<Long> snapshotIdList,
    Long logSeq,
    boolean needUpdateNextLog,
    List<LogInfoDto> logInfoList
) {

}
