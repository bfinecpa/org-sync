package org.orgsync.core.spec;

/**
 * Strategy for writing incoming records.
 */
public enum WriteMode {
    UPSERT,
    INSERT_ONLY
}
