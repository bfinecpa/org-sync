package org.orgsync.core.state;

import java.util.Optional;

/**
 * Stores and retrieves synchronization cursors.
 */
public interface LogSeqRepository {

    Optional<Long> loadLogSeq(String companyUuid);

    void saveCursor(String companyUuid, Long logSeq);
}
