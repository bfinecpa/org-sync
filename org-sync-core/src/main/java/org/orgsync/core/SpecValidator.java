package org.orgsync.core;

import java.util.Map;

/**
 * Performs basic validations on the parsed YAML sync specification.
 */
public class SpecValidator {

    public void validate(YamlSyncSpec spec) {
        if (spec.getDomains().isEmpty()) {
            throw new IllegalArgumentException("At least one domain mapping must be provided");
        }
        for (Map.Entry<String, YamlSyncSpec.DomainProjection> entry : spec.getDomains().entrySet()) {
            validateDomain(entry.getKey(), entry.getValue());
        }
    }

    private void validateDomain(String domain, YamlSyncSpec.DomainProjection projection) {
        if (projection.table() == null || projection.table().isBlank()) {
            throw new IllegalArgumentException("Domain " + domain + " requires a target table");
        }
        if (projection.fields() == null || projection.fields().isEmpty()) {
            throw new IllegalArgumentException("Domain " + domain + " requires at least one field mapping");
        }
    }
}
