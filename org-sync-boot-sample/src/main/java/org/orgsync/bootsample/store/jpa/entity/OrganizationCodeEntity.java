package org.orgsync.bootsample.store.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.orgsync.core.dto.type.OrganizationCodeType;

@Entity
@Table(name = "org_sync_organization_code")
public class OrganizationCodeEntity {

    @Id
    private Long id;

    private Long companyId;

    private String code;

    @Enumerated(EnumType.STRING)
    private OrganizationCodeType type;

    private String name;

    private int sortOrder;

    protected OrganizationCodeEntity() {
    }

    public OrganizationCodeEntity(Long id, Long companyId, String code, OrganizationCodeType type, String name,
                                  int sortOrder) {
        this.id = id;
        this.companyId = companyId;
        this.code = code;
        this.type = type;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getCode() {
        return code;
    }

    public OrganizationCodeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
