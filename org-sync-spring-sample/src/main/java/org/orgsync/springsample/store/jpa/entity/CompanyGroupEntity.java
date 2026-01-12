package org.orgsync.springsample.store.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "org_sync_company_group")
public class CompanyGroupEntity {

    @Id
    private Long id;

    protected CompanyGroupEntity() {
    }

    public CompanyGroupEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
