package org.orgsync.core.spec;

import java.util.Collections;
import java.util.List;

/**
 * Domain-level projection options.
 */
public record DomainSpec(boolean enabled,
                         String table,
                         String pk,
                         WriteMode writeMode,
                         DeleteMode deleteMode,
                         List<FieldMapping> fieldMappings,
                         RecordFilter filter,
                         EventSpec events) {

    public DomainSpec {
        fieldMappings = fieldMappings == null ? List.of() : Collections.unmodifiableList(fieldMappings);
        writeMode = writeMode == null ? WriteMode.UPSERT : writeMode;
        deleteMode = deleteMode == null ? DeleteMode.HARD_DELETE : deleteMode;
        filter = filter == null ? RecordFilter.acceptAll() : filter;
        events = events == null ? new EventSpec(false, List.of()) : events;
    }

    public boolean hasMappings() {
        return !fieldMappings.isEmpty();
    }
}
