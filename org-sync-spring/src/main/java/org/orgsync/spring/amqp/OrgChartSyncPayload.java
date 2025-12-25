package org.orgsync.spring.amqp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Payload for RabbitMQ company change events.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgChartSyncPayload(String companyUuid, Long logSeq) {
}
