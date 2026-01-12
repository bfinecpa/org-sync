package org.orgsync.bootsample.store.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "org_sync_multi_language")
public class MultiLanguageEntity {

    @EmbeddedId
    private MultiLanguageId id;

    @Column(name = "language_value")
    private String value;

    protected MultiLanguageEntity() {
    }

    public MultiLanguageEntity(MultiLanguageId id, String value) {
        this.id = id;
        this.value = value;
    }

    public MultiLanguageId getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
