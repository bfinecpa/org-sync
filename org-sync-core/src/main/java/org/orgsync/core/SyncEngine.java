package org.orgsync.core;

import java.util.Objects;
import java.util.Optional;

/**
 * Coordinates synchronization by pulling data from the org chart server and applying it
 * to a downstream database.
 */
public class SyncEngine {

    private final OrgChartClient client;
    private final SyncStateRepository stateRepository;
    private final JdbcApplier jdbcApplier;
    private final DomainEventPublisher eventPublisher;
    private final LockManager lockManager;

    public SyncEngine(OrgChartClient client,
                      SyncStateRepository stateRepository,
                      JdbcApplier jdbcApplier,
                      DomainEventPublisher eventPublisher,
                      LockManager lockManager) {
        this.client = Objects.requireNonNull(client, "client");
        this.stateRepository = Objects.requireNonNull(stateRepository, "stateRepository");
        this.jdbcApplier = Objects.requireNonNull(jdbcApplier, "jdbcApplier");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.lockManager = Objects.requireNonNull(lockManager, "lockManager");
    }

    public void synchronizeCompany(String companyId) {
        lockManager.withLock(companyId, () -> doSynchronize(companyId));
    }

    private void doSynchronize(String companyId) {
        Optional<String> cursor = stateRepository.loadCursor(companyId);
        SyncResponse response = client.fetchChanges(companyId, cursor.orElse(null));
        if (response.needSnapshot()) {
            applySnapshot(companyId, response);
        } else {
            applyDelta(companyId, response);
        }
        stateRepository.saveCursor(companyId, response.nextCursor());
    }

    private void applySnapshot(String companyId, SyncResponse response) {
        jdbcApplier.applySnapshot(companyId, response);
        eventPublisher.publishSnapshotApplied(companyId, response.nextCursor(), response.processedDomains());
    }

    private void applyDelta(String companyId, SyncResponse response) {
        jdbcApplier.applyDelta(companyId, response);
        response.events().forEach(eventPublisher::publishDomainEvent);
    }
}
