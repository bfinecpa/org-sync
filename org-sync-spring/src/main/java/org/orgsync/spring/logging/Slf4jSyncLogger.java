package org.orgsync.spring.logging;

import org.orgsync.core.logging.SyncLogger;
import org.slf4j.Logger;

public class Slf4jSyncLogger implements SyncLogger {

    private final Logger logger;

    public Slf4jSyncLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (throwable == null) {
            logger.error(message);
            return;
        }
        logger.error(message, throwable);
    }
}
