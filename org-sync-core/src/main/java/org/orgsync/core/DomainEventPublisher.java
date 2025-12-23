package org.orgsync.core;

/**
 * Publishes synchronization domain events to the hosting application.
 */
public interface DomainEventPublisher {

    void publishDomainEvent(DomainEvent event);

    void publishSnapshotApplied(String companyId, String cursor, Iterable<String> domains);
}
