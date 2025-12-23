package org.orgsync.core.spec;

import static org.orgsync.core.Constants.ERROR_PREFIX;

import java.util.Map;

/**
 * Performs basic validations on sync specifications loaded via YAML or DSL.
 */
public class SpecValidator {

    public void validate(YamlSyncSpec spec) {
        if (spec.getDomains().isEmpty()) {
            throw new IllegalArgumentException(ERROR_PREFIX + "At least one domain mapping must be provided");
        }
        for (Map.Entry<String, YamlSyncSpec.DomainProjection> entry : spec.getDomains().entrySet()) {
            validateDomain(entry.getKey(), entry.getValue());
        }
    }

    public void validate(OrgSyncSpec spec) {
        if (spec.domains().isEmpty()) {
            throw new IllegalArgumentException(ERROR_PREFIX + "At least one domain mapping must be provided");
        }
        for (Map.Entry<String, DomainSpec> entry : spec.domains().entrySet()) {
            validateDomain(entry.getKey(), entry.getValue());
        }
    }

    private void validateDomain(String domain, YamlSyncSpec.DomainProjection projection) {
        if (projection.table() == null || projection.table().isBlank()) {
            throw new IllegalArgumentException(ERROR_PREFIX + "Domain " + domain + " requires a target table");
        }
        if (projection.fields() == null || projection.fields().isEmpty()) {
            throw new IllegalArgumentException(ERROR_PREFIX + "Domain " + domain + " requires at least one field mapping");
        }
    }

    private void validateDomain(String domain, DomainSpec domainSpec) {
        if (!domainSpec.enabled()) {
            return;
        }
        if (domainSpec.table() == null || domainSpec.table().isBlank()) {
            throw new IllegalArgumentException(ERROR_PREFIX + "Domain " + domain + " requires a target table");
        }
        if (domainSpec.fieldMappings() == null || domainSpec.fieldMappings().isEmpty()) {
            throw new IllegalArgumentException(ERROR_PREFIX + "Domain " + domain + " requires at least one field mapping");
        }
    }
}
