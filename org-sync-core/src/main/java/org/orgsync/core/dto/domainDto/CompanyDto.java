package org.orgsync.core.dto.domainDto;

public class CompanyDto {
    private Long id;
    private String uuid;
    private Long companyGroupId;

    public CompanyDto() {
    }

    public CompanyDto(Long id, String uuid, Long companyGroupId) {
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

    public void updateCompanyGroupId(Long companyGroupId) {
        this.companyGroupId = companyGroupId;
    }
}
