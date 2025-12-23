package org.orgsync.spring;

import org.orgsync.core.LockManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple in-memory lock manager to demonstrate serialization by company identifier.
 */
public class InMemoryLockManager implements LockManager {

    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void withLock(String companyId, Runnable runnable) {
        ReentrantLock lock = locks.computeIfAbsent(companyId, key -> new ReentrantLock());
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }
}
