package org.orgsync.core.client;

import org.orgsync.core.engine.SyncResponse;

/**
 * Fetches snapshot or delta data from the upstream org chart server.
 */
public interface OrgChartClient {

    /**
     * Retrieves changes after the given cursor for a company.
     * @param companyId company identifier
     * @param logSeq last processed cursor or {@code null} when starting
     * @return snapshot or delta response
     */
    SyncResponse fetchChanges(String companyId, Long logSeq);
}
