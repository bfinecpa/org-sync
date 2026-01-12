package org.orgsync.bootsample.store.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "org_sync_company")
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    private Long companyGroupId;

    protected CompanyEntity() {
    }

    public CompanyEntity(String uuid) {
        this.uuid = uuid;
    }

    public CompanyEntity(Long id, String uuid, Long companyGroupId) {
        this.id = id;
        this.uuid = uuid;
        this.companyGroupId = companyGroupId;
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Long getCompanyGroupId() {
        return companyGroupId;
    }

    public void setCompanyGroupId(Long companyGroupId) {
        this.companyGroupId = companyGroupId;
    }
}
