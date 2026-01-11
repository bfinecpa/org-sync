package org.orgsync.core.dto.domainDto;

import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.deltaDto.Settable;

public class CompanyGroupDto {

    private Long id;

    public CompanyGroupDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


}
