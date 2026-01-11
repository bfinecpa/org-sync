package org.orgsync.core.client;

import org.orgsync.core.dto.ProvisionSequenceDto;
import org.orgsync.core.dto.snapshotDto.SnapshotDto;

/**
 * Fetches snapshot or delta data from the upstream org chart server.
 */
public interface OrgChartClient {

    /**
     * Retrieves changes after the given cursor for a company.
     * @param companyUuid company identifier
     * @param logSeq last processed cursor or {@code null} when starting
     * @return snapshot or delta response
     */
    ProvisionSequenceDto fetchChanges(String companyUuid, Long logSeq);

    SnapshotDto fetchSnapshot(String companyUuid, Long snapshotId);


}
