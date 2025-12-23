package org.orgsync.core.spec;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Parsed representation of the YAML-based projection specification.
 */
public class YamlSyncSpec {

    private final Map<String, DomainProjection> domains;

    public YamlSyncSpec(Map<String, DomainProjection> domains) {
        this.domains = domains == null ? Collections.emptyMap() : Collections.unmodifiableMap(domains);
    }

    public Map<String, DomainProjection> getDomains() {
        return domains;
    }

    public record DomainProjection(String table, Map<String, String> fields) {
        public DomainProjection {
            Objects.requireNonNull(table, "table");
            fields = fields == null ? Collections.emptyMap() : Collections.unmodifiableMap(fields);
        }
    }
}
