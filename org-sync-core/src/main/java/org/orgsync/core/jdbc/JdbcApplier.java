package org.orgsync.core.jdbc;

import org.orgsync.core.engine.SyncResponse;
import org.orgsync.core.spec.OrgSyncSpec;
import org.orgsync.core.spec.YamlSyncSpec;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Applies snapshot or delta responses into JDBC-accessible storage.
 */
public class JdbcApplier {

    private final DataSource dataSource;
    private final OrgSyncSpec syncSpec;

    public JdbcApplier(DataSource dataSource, OrgSyncSpec syncSpec) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
        this.syncSpec = Objects.requireNonNull(syncSpec, "syncSpec");
    }

    public JdbcApplier(DataSource dataSource, YamlSyncSpec syncSpec) {
        this(dataSource, OrgSyncSpec.fromYaml(syncSpec));
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

    public OrgSyncSpec getSyncSpec() {
        return syncSpec;
    }
}
