package org.orgsync.core.dto.domainDto;

import org.orgsync.core.Constants;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.type.OrganizationCodeType;
import org.orgsync.core.dto.deltaDto.Settable;

public class OrganizationCodeDto{

    private Long id;
    private Long companyId;
    private String code;
    private OrganizationCodeType type;
    private String name;
    private int sortOrder;

    public OrganizationCodeDto(Long id, Long companyId, String code, OrganizationCodeType type, String name,
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

    public void update(String code, OrganizationCodeType type, String name, Integer sortOrder) {
        if (code != null) {
            this.code = code;
        }
        if (type != null) {
            this.type = type;
        }
        if (name != null) {
            this.name = name;
        }
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
    }
}
