package org.orgsync.spring.amqp;

/**
 * Payload for RabbitMQ company change events.
 */
public record CompanyChangeMessage(String companyId) {
}
