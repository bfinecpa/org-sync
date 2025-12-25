package org.orgsync.boot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configures how the JDBC lock manager finds the company row to lock.
 */
@ConfigurationProperties(prefix = "orgsync.lock.company")
public class CompanyLockProperties {

    /**
     * Table name that stores company identifiers.
     */
    private String table = "company";

    /**
     * Column name that contains the company UUID.
     */
    private String uuidColumn = "uuid";

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getUuidColumn() {
        return uuidColumn;
    }

    public void setUuidColumn(String uuidColumn) {
        this.uuidColumn = uuidColumn;
    }
}
