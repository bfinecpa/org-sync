package org.orgsync.core.logging;

public interface SyncLogger {

    void info(String message);

    void warn(String message);

    void error(String message, Throwable throwable);

    static SyncLogger noop() {
        return new SyncLogger() {
            @Override
            public void info(String message) {
            }

            @Override
            public void warn(String message) {
            }

            @Override
            public void error(String message, Throwable throwable) {
            }
        };
    }
}
