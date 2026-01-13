package org.orgsync.core.dto.domainDto;

import java.util.Map;
import org.orgsync.core.dto.type.DepartmentStatus;
import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.deltaDto.Settable;

public class DepartmentDto implements Settable {

    private Long id;
    private Long companyId;
    private String name;
    private Long parentId;
    private int sortOrder;
    private String code;
    private String alias;
    private String emailId;
    private DepartmentStatus status;
    private String departmentPath;

    public DepartmentDto(Long id, Long companyId, String name, Long parentId, int sortOrder, String code, String alias,
        String emailId, DepartmentStatus status, String departmentPath) {
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


    public void updateParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void update(String name, Long parentId, Integer sortOrder, String code, String alias, String emailId,
        DepartmentStatus status, String departmentPath) {
        if (name != null) {
            this.name = name;
        }
        if (parentId != null) {
            this.parentId = parentId;
        }
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
        if (code != null) {
            this.code = code;
        }
        if (alias != null) {
            this.alias = alias;
        }
        if (emailId != null) {
            this.emailId = emailId;
        }
        if (status != null) {
            this.status = status;
        }
        if (departmentPath != null) {
            this.departmentPath = departmentPath;
        }
    }
}
