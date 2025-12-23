package org.orgsync.core;

import java.util.Optional;

/**
 * Stores and retrieves synchronization cursors.
 */
public interface SyncStateRepository {

    Optional<String> loadCursor(String companyId);

    void saveCursor(String companyId, String nextCursor);
}
