package org.orgsync.core.spec;

/**
 * Convenience filters for common record filtering needs.
 */
public final class RecordFilters {

    private RecordFilters() {
    }

    public static RecordFilter prefix(String field, String prefix) {
        return record -> {
            Object value = record.get(field);
            return value != null && value.toString().startsWith(prefix);
        };
    }
}
