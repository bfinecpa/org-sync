package org.orgsync.core;

/**
 * Shared library constants.
 */
public final class Constants {

    public static final String ORG_SYNC_PREFIX = "[org-sync] ";
    public static final String COMPANY_UUID = "companyUuid: ";
    public static final String ORG_SYNC_LOG_PREFIX = ORG_SYNC_PREFIX + COMPANY_UUID;
    public static final String SNAPSHOT = "[snapshot] ";
    public static final String DELTA = "[delta] ";
    public static final String SNAPSHOT_LOG_PREFIX = ORG_SYNC_PREFIX + SNAPSHOT +  COMPANY_UUID;
    public static final String DELTA_LOG_PREFIX = ORG_SYNC_PREFIX + DELTA +  COMPANY_UUID;


    private Constants() {
    }
}
