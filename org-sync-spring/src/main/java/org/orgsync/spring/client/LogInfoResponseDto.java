package org.orgsync.spring.client;

/**
 * Representation of a single log entry returned by the upstream provision API.
 */
public record LogInfoResponseDto(
        String domain,
        Long domainId,
        String fieldName,
        Object updatedValue,
        String logType
) {
}
