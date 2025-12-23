package org.orgsync.core.engine;

import org.orgsync.core.event.DomainEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents either a snapshot or delta response from the org chart service.
 */
public class SyncResponse {

    private final boolean needSnapshot;
    private final String nextCursor;
    private final Set<String> processedDomains;
    private final List<DomainEvent> events;

    public SyncResponse(boolean needSnapshot, String nextCursor, Set<String> processedDomains, List<DomainEvent> events) {
        this.needSnapshot = needSnapshot;
        this.nextCursor = nextCursor;
        this.processedDomains = processedDomains == null ? Collections.emptySet() : Collections.unmodifiableSet(processedDomains);
        this.events = events == null ? Collections.emptyList() : Collections.unmodifiableList(events);
    }

    public boolean needSnapshot() {
        return needSnapshot;
    }

    public String nextCursor() {
        return nextCursor;
    }

    public Set<String> processedDomains() {
        return processedDomains;
    }

    public List<DomainEvent> events() {
        return events;
    }
}
