package org.orgsync.core.spec;

import java.util.Collections;
import java.util.List;

/**
 * Event emission configuration for a domain.
 */
public record EventSpec(boolean entityEvents, List<String> fieldEvents) {

    public EventSpec {
        fieldEvents = fieldEvents == null ? List.of() : Collections.unmodifiableList(fieldEvents);
    }
}
