package org.orgsync.core.spec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fluent builder for {@link OrgSyncSpec}. See {@link OrgSyncSpec#orgsyncSpec(Consumer)} for
 * the entry point.
 */
public class OrgSyncSpecBuilder {

    private StateSpec state = StateSpec.defaults();
    private boolean validateSchemaOnStartup;
    private final Map<String, DomainSpecBuilder> domains = new LinkedHashMap<>();

    public OrgSyncSpecBuilder state(Consumer<StateSpecBuilder> customizer) {
        Objects.requireNonNull(customizer, "customizer");
        StateSpecBuilder builder = new StateSpecBuilder(state);
        customizer.accept(builder);
        this.state = builder.build();
        return this;
    }

    public OrgSyncSpecBuilder validateSchemaOnStartup(boolean validate) {
        this.validateSchemaOnStartup = validate;
        return this;
    }

    public OrgSyncSpecBuilder domain(String name, Consumer<DomainSpecBuilder> customizer) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(customizer, "customizer");
        DomainSpecBuilder builder = new DomainSpecBuilder();
        customizer.accept(builder);
        domains.put(name, builder);
        return this;
    }

    OrgSyncSpec build() {
        Map<String, DomainSpec> builtDomains = new LinkedHashMap<>();
        for (Map.Entry<String, DomainSpecBuilder> entry : domains.entrySet()) {
            builtDomains.put(entry.getKey(), entry.getValue().build());
        }
        return new OrgSyncSpec(state, validateSchemaOnStartup, builtDomains);
    }

    public static class StateSpecBuilder {
        private String table;
        private String companyIdColumn;
        private String cursorColumn;

        public StateSpecBuilder(StateSpec existing) {
            this.table = existing.table();
            this.companyIdColumn = existing.companyIdColumn();
            this.cursorColumn = existing.cursorColumn();
        }

        public StateSpecBuilder table(String table) {
            this.table = table;
            return this;
        }

        public StateSpecBuilder companyIdColumn(String companyIdColumn) {
            this.companyIdColumn = companyIdColumn;
            return this;
        }

        public StateSpecBuilder cursorColumn(String cursorColumn) {
            this.cursorColumn = cursorColumn;
            return this;
        }

        public StateSpec build() {
            return new StateSpec(table, companyIdColumn, cursorColumn);
        }
    }

    public static class DomainSpecBuilder {
        private boolean enabled = true;
        private String table;
        private String pk;
        private WriteMode writeMode = WriteMode.UPSERT;
        private DeleteMode deleteMode = DeleteMode.HARD_DELETE;
        private final List<FieldMapping> mappings = new ArrayList<>();
        private RecordFilter filter = RecordFilter.acceptAll();
        private EventSpecBuilder eventBuilder = new EventSpecBuilder();

        public DomainSpecBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public DomainSpecBuilder table(String table) {
            this.table = table;
            return this;
        }

        public DomainSpecBuilder pk(String pk) {
            this.pk = pk;
            return this;
        }

        public DomainSpecBuilder writeMode(WriteMode writeMode) {
            this.writeMode = writeMode;
            return this;
        }

        public DomainSpecBuilder deleteMode(DeleteMode deleteMode) {
            this.deleteMode = deleteMode;
            return this;
        }

        public DomainSpecBuilder map(String field, String column, SqlColumnType type, Integer length, boolean nullable) {
            mappings.add(new FieldMapping(field, column, type, length, nullable));
            return this;
        }

        public DomainSpecBuilder filter(RecordFilter filter) {
            this.filter = filter;
            return this;
        }

        public DomainSpecBuilder emit(Consumer<EventSpecBuilder> customizer) {
            Objects.requireNonNull(customizer, "customizer");
            EventSpecBuilder builder = new EventSpecBuilder(eventBuilder);
            customizer.accept(builder);
            this.eventBuilder = builder;
            return this;
        }

        DomainSpec build() {
            return new DomainSpec(enabled, table, pk, writeMode, deleteMode, mappings, filter, eventBuilder.build());
        }
    }

    public static class EventSpecBuilder {
        private boolean entityEvents;
        private final List<String> fieldEvents = new ArrayList<>();

        public EventSpecBuilder() {
        }

        public EventSpecBuilder(EventSpecBuilder copy) {
            if (copy != null) {
                this.entityEvents = copy.entityEvents;
                this.fieldEvents.addAll(copy.fieldEvents);
            }
        }

        public EventSpecBuilder entityEvents(boolean entityEvents) {
            this.entityEvents = entityEvents;
            return this;
        }

        public EventSpecBuilder fieldEvents(String... fields) {
            if (fields != null) {
                fieldEvents.addAll(List.of(fields));
            }
            return this;
        }

        EventSpec build() {
            return new EventSpec(entityEvents, fieldEvents);
        }
    }
}
