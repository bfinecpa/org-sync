package org.orgsync.core.spec;

/**
 * Strategy for handling deletions for a domain.
 */
public enum DeleteMode {
    HARD_DELETE,
    SOFT_DELETE,
    IGNORE
}
