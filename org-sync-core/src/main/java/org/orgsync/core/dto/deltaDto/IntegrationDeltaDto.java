package org.orgsync.core.dto.deltaDto;

import org.orgsync.core.dto.LogInfoDto;

public class IntegrationDeltaDto implements Settable {
    private Long id;
    private String userIds;

    public IntegrationDeltaDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUserIds() {
        return userIds;
    }

    @Override
    public void set(LogInfoDto logInfoDto) {
        setId(logInfoDto.domainId());
        setUpdateValue(logInfoDto.updatedValue());
    }

    private void setId(Long id) {
        this.id = id;
    }
    private void setUpdateValue(Object updateValue) {
        this.userIds = updateValue.toString();
    }
}
