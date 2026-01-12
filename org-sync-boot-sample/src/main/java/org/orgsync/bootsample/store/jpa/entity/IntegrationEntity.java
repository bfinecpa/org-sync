package org.orgsync.bootsample.store.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "org_sync_integration")
public class IntegrationEntity {

    @Id
    private Long id;

    protected IntegrationEntity() {
    }

    public IntegrationEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
