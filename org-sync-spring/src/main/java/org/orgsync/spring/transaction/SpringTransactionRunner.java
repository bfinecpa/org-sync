package org.orgsync.spring.transaction;

import org.orgsync.core.transaction.TransactionRunner;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Executes work within a Spring-managed transaction.
 */
public class SpringTransactionRunner implements TransactionRunner {

    private final TransactionTemplate transactionTemplate;

    public SpringTransactionRunner(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void run(Runnable action) {
        transactionTemplate.execute(status -> {
            action.run();
            return null;
        });
    }
}
