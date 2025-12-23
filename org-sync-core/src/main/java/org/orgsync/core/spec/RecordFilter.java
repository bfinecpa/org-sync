package org.orgsync.core.spec;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Filter that determines if an incoming record should be stored.
 */
@FunctionalInterface
public interface RecordFilter extends Predicate<Map<String, Object>> {

    static RecordFilter acceptAll() {
        return record -> true;
    }
}
