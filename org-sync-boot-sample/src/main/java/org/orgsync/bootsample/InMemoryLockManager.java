package org.orgsync.bootsample;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import org.orgsync.core.lock.LockManager;

public class InMemoryLockManager implements LockManager {

    private final ConcurrentMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void withLock(String companyUuid, Runnable runnable) {
        ReentrantLock lock = locks.computeIfAbsent(companyUuid, key -> new ReentrantLock());
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }
}
