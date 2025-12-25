package org.orgsync.core.lock;

/**
 * Coordinates company-level locking to ensure idempotent processing.
 */
public interface LockManager {

    void withLock(String companyUuid, Runnable runnable);
}
