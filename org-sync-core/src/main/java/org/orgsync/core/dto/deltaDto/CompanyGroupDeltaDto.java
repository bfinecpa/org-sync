package org.orgsync.core.dto.deltaDto;

import org.orgsync.core.dto.LogInfoDto;

public class CompanyGroupDeltaDto implements Settable {

    private Long id;

    private String companyUuids;

    public CompanyGroupDeltaDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCompanyUuids() {
        return companyUuids;
    }

    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null || logInfoDto.updatedValue() == null) {
            return;
        }

        setId(logInfoDto.domainId());
        setUpdateValue(logInfoDto.updatedValue());
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setUpdateValue(Object companyUuids) {
        this.companyUuids = companyUuids.toString();
    }
}
