package org.orgsync.spring;

import org.orgsync.core.DomainEvent;
import org.orgsync.core.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;

/**
 * Bridges {@link DomainEventPublisher} to Spring's {@link ApplicationEventPublisher}.
 */
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = Objects.requireNonNull(applicationEventPublisher, "applicationEventPublisher");
    }

    @Override
    public void publishDomainEvent(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishSnapshotApplied(String companyId, String cursor, Iterable<String> domains) {
        applicationEventPublisher.publishEvent(new SnapshotAppliedEvent(companyId, cursor, domains));
    }
}
