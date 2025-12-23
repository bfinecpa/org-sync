package org.orgsync.core.spec;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * In-memory representation of the org-sync specification. Users can construct an instance
 * via the {@link #orgsyncSpec(Consumer)} builder DSL instead of providing a YAML file.
 */
public record OrgSyncSpec(StateSpec state, boolean validateSchemaOnStartup, Map<String, DomainSpec> domains) {

    public OrgSyncSpec {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(domains, "domains");
        domains = Collections.unmodifiableMap(new LinkedHashMap<>(domains));
    }

    public static OrgSyncSpec orgsyncSpec(Consumer<OrgSyncSpecBuilder> customizer) {
        Objects.requireNonNull(customizer, "customizer");
        OrgSyncSpecBuilder builder = new OrgSyncSpecBuilder();
        customizer.accept(builder);
        return builder.build();
    }

    public static OrgSyncSpec fromYaml(YamlSyncSpec yamlSyncSpec) {
        Objects.requireNonNull(yamlSyncSpec, "yamlSyncSpec");
        OrgSyncSpecBuilder builder = new OrgSyncSpecBuilder();
        yamlSyncSpec.getDomains().forEach((name, projection) ->
                builder.domain(name, domain -> {
                    domain.table(projection.table());
                    projection.fields().forEach((field, column) ->
                            domain.map(field, column, SqlColumnType.VARCHAR, null, true));
                }));
        return builder.build();
    }
}
