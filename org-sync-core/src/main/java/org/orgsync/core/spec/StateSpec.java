package org.orgsync.core.spec;

import java.util.Objects;

/**
 * Where to store synchronization cursors.
 */
public record StateSpec(String table, String companyIdColumn, String cursorColumn) {

    public StateSpec {
        Objects.requireNonNull(table, "table");
        Objects.requireNonNull(companyIdColumn, "companyIdColumn");
        Objects.requireNonNull(cursorColumn, "cursorColumn");
    }

    public static StateSpec defaults() {
        return new StateSpec("orgsync_state", "company_id", "cursor");
    }
}
