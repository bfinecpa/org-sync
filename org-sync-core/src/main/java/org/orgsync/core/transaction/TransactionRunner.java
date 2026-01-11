package org.orgsync.core.transaction;

/**
 * Abstraction for executing work within a transaction.
 */
@FunctionalInterface
public interface TransactionRunner {

    void run(Runnable action);

    static TransactionRunner noOp() {
        return Runnable::run;
    }
}
