package org.orgsync.core.engine;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.jdbc.JdbcApplier;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.state.LogSeqRepository;

import java.util.Objects;
import java.util.Optional;

/**
 * Coordinates synchronization by pulling data from the org chart server and applying it
 * to a downstream database.
 */
public class SyncEngine {

    private final OrgChartClient client;
    private final LogSeqRepository stateRepository;
    private final JdbcApplier jdbcApplier;
    private final DomainEventPublisher eventPublisher;
    private final LockManager lockManager;

    public SyncEngine(OrgChartClient client,
                      LogSeqRepository stateRepository,
                      JdbcApplier jdbcApplier,
                      DomainEventPublisher eventPublisher,
                      LockManager lockManager) {
        this.client = Objects.requireNonNull(client, "client");
        this.stateRepository = Objects.requireNonNull(stateRepository, "stateRepository");
        this.jdbcApplier = Objects.requireNonNull(jdbcApplier, "jdbcApplier");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.lockManager = Objects.requireNonNull(lockManager, "lockManager");
    }

    public void synchronizeCompany(String companyUuid, Long logSeq) {
        lockManager.withLock(companyUuid, () -> doSynchronize(companyUuid, logSeq));
    }

    private void doSynchronize(String companyUuid, Long newLogSeq) {
        Long existedLogSeq = stateRepository.loadLogSeq(companyUuid).orElse(-1L);
        if (newLogSeq <= existedLogSeq) {
            return;
        }
        SyncResponse response = client.fetchChanges(companyUuid, existedLogSeq);
        if (response.needSnapshot()) {
            applySnapshot(companyUuid, response);
        } else {
            applyDelta(companyUuid, response);
        }
        stateRepository.saveCursor(companyUuid, response.nextCursor());
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
