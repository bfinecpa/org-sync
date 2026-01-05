package org.orgsync.core.jdbc;

import org.orgsync.core.engine.SyncResponse;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Applies snapshot or delta responses into JDBC-accessible storage.
 */
public class JdbcApplier {

    private final DataSource dataSource;

    public JdbcApplier(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource");
    }

    public void applySnapshot(String companyId, SyncResponse response) {
        // TODO: implement chunked snapshot writes and validation
    }

    public void applyDelta(String companyId, SyncResponse response) {
        // TODO: implement upsert/delete operations and event mapping
    }

    public void insertRow(String tableName, LinkedHashMap<String, Object> columnValues) {
        Objects.requireNonNull(tableName, "tableName");
        Objects.requireNonNull(columnValues, "columnValues");
        if (columnValues.isEmpty()) {
            return;
        }

        StringJoiner columnJoiner = new StringJoiner(", ");
        StringJoiner placeholderJoiner = new StringJoiner(", ");
        columnValues.forEach((column, value) -> {
            columnJoiner.add(column);
            placeholderJoiner.add("?");
        });

        String sql = "INSERT INTO " + tableName + " (" + columnJoiner + ") VALUES (" + placeholderJoiner + ")";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (Object value : columnValues.values()) {
                statement.setObject(index++, value);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert row into " + tableName, e);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
