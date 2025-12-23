package org.orgsync.spring.event;

import java.util.List;

/**
 * Spring application event describing a completed snapshot application.
 */
public class SnapshotAppliedEvent {

    private final String companyId;
    private final String cursor;
    private final List<String> domains;

    public SnapshotAppliedEvent(String companyId, String cursor, Iterable<String> domains) {
        this.companyId = companyId;
        this.cursor = cursor;
        this.domains = domains == null ? List.of() : List.copyOf(domains);
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCursor() {
        return cursor;
    }

    public List<String> getDomains() {
        return domains;
    }
}
