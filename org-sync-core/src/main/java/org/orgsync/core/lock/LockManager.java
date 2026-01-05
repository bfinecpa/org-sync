package org.orgsync.core.lock;

/**
 * Coordinates company-level locking to ensure idempotent processing.
 * <p>
 * The library only depends on this interface; applications are free to provide
 * their own implementation that matches their infrastructure (e.g. SQL
 * row-level lock via {@code SELECT ... FOR UPDATE}, Redis-based mutex, etc.).
 */
public interface LockManager {

    void withLock(String companyUuid, Runnable runnable);
}
