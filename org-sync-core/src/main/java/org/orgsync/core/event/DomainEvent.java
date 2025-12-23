package org.orgsync.core.event;

import java.util.Map;
import java.util.Objects;

/**
 * Simplified domain event representation emitted after delta processing.
 */
public class DomainEvent {

    public enum Type {
        ENTITY_CREATED,
        ENTITY_UPDATED,
        ENTITY_DELETED,
        FIELD_UPDATED,
        SNAPSHOT_APPLIED
    }

    private final Type type;
    private final String domain;
    private final String key;
    private final Map<String, Object> payload;

    public DomainEvent(Type type, String domain, String key, Map<String, Object> payload) {
        this.type = Objects.requireNonNull(type, "type");
        this.domain = Objects.requireNonNull(domain, "domain");
        this.key = Objects.requireNonNull(key, "key");
        this.payload = payload;
    }

    public Type type() {
        return type;
    }

    public String domain() {
        return domain;
    }

    public String key() {
        return key;
    }

    public Map<String, Object> payload() {
        return payload;
    }
}
