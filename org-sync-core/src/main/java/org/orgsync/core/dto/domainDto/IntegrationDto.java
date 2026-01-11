package org.orgsync.core.dto.domainDto;


import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.deltaDto.Settable;

public class IntegrationDto {
    private Long id;

    public Long getId() {
        return id;
    }

    public IntegrationDto(Long id) {
        this.id = id;
    }
}
