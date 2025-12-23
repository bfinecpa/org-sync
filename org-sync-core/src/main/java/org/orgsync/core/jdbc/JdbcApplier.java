package org.orgsync.core.jdbc;

import org.orgsync.core.engine.SyncResponse;
import org.orgsync.core.spec.YamlSyncSpec;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Applies snapshot or delta responses into JDBC-accessible storage.
 */
public class JdbcApplier {

    private final DataSource dataSource;
    private final YamlSyncSpec syncSpec;

    public JdbcApplier(DataSource dataSource, YamlSyncSpec syncSpec) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
        this.syncSpec = Objects.requireNonNull(syncSpec, "syncSpec");
    }

    public void applySnapshot(String companyId, SyncResponse response) {
        // TODO: implement chunked snapshot writes and validation
    }

    public void applyDelta(String companyId, SyncResponse response) {
        // TODO: implement upsert/delete operations and event mapping
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public YamlSyncSpec getSyncSpec() {
        return syncSpec;
    }
}
