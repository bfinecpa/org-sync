package org.orgsync.springsample.store.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.orgsync.core.dto.type.DepartmentStatus;

@Entity
@Table(name = "org_sync_department")
public class DepartmentEntity {

    @Id
    private Long id;

    private Long companyId;

    private String name;

    private Long parentId;

    private int sortOrder;

    private String code;

    private String alias;

    private String emailId;

    @Enumerated(EnumType.STRING)
    private DepartmentStatus status;

    private String departmentPath;

    protected DepartmentEntity() {
    }

    public DepartmentEntity(Long id, Long companyId, String name, Long parentId, int sortOrder, String code,
                            String alias, String emailId, DepartmentStatus status, String departmentPath) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.parentId = parentId;
        this.sortOrder = sortOrder;
        this.code = code;
        this.alias = alias;
        this.emailId = emailId;
        this.status = status;
        this.departmentPath = departmentPath;
    }

    public Long getId() {
        return id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public String getCode() {
        return code;
    }

    public String getAlias() {
        return alias;
    }

    public String getEmailId() {
        return emailId;
    }

    public DepartmentStatus getStatus() {
        return status;
    }

    public String getDepartmentPath() {
        return departmentPath;
    }
}
