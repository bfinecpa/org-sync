package org.orgsync.core.dto;

import org.orgsync.core.dto.type.DomainType;
import org.orgsync.core.dto.type.LogType;

/**
 * Representation of a single log entry returned by the upstream provision API.
 */
public record LogInfoDto(
    DomainType domain,
    Long domainId,
    String fieldName,
    Object updatedValue,
    LogType logType
) {

    @Override
    public DomainType domain() {
        return DomainType.of(domain, fieldName);
    }
}
