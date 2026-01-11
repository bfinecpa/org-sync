package org.orgsync.core.service;

import java.util.Optional;

/**
 * Stores and retrieves synchronization cursors.
 */
public interface OrgSyncLogSeqService {

    Optional<Long> getLogSeq(String companyUuid);

    void saveLogSeq(Long companyId, Long logSeq);
}
