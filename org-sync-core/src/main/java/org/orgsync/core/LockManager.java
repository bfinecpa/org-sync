package org.orgsync.core;

/**
 * Coordinates company-level locking to ensure idempotent processing.
 */
public interface LockManager {

    void withLock(String companyId, Runnable runnable);
}
