package org.orgsync.core.dto;

public class CompanyGroupDto implements Settable {

    private Long id;

    public Long getId() {
        return id;
    }

    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null || logInfoDto.updatedValue() == null) {
            return;
        }

        if ("id".equals(logInfoDto.fieldName())) {
            setId(Long.valueOf(logInfoDto.updatedValue().toString()));
        }
    }

    private void setId(Long id) {
        this.id = id;
    }
}
