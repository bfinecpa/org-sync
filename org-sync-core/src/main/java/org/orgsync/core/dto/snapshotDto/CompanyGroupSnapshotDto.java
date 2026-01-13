package org.orgsync.core.dto.snapshotDto;

import java.util.List;

public class CompanyGroupSnapshotDto {

    private Long id;

    private List<String> companyUuidList;

    public CompanyGroupSnapshotDto() {
    }

    public CompanyGroupSnapshotDto(Long id, List<String> companyUuidList) {
        this.id = id;
        this.companyUuidList = companyUuidList;
    }

    public Long getId() {
        return id;
    }


    public List<String> getCompanyUuidList() {
        return companyUuidList;
    }
}
