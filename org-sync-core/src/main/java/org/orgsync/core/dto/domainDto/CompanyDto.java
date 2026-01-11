package org.orgsync.core.dto.domainDto;

public class CompanyDto {
    private Long id;
    private String uuid;
    private Long companyGroupId;

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Long getCompanyGroupId() {
        return companyGroupId;
    }

    public void updateCompanyGroupId(Long companyGroupId) {
        this.companyGroupId = companyGroupId;
    }
}
