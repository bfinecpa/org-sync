package org.orgsync.core.spec;

import java.util.Objects;

/**
 * Mapping from a canonical source field to a target column.
 */
public record FieldMapping(String field, String column, SqlColumnType columnType, Integer length, boolean nullable) {

    public FieldMapping {
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(column, "column");
        Objects.requireNonNull(columnType, "columnType");
    }
}
