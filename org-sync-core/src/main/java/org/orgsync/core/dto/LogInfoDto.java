package org.orgsync.core.dto;

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
}
