package org.orgsync.core.dto;

public class CompanyDto implements Settable {
    private Long id;
    private String uuid;
    private Long companyGroup;

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Long getCompanyGroup() {
        return companyGroup;
    }

    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null || logInfoDto.updatedValue() == null) {
            return;
        }

        Object updatedValue = logInfoDto.updatedValue();
        switch (logInfoDto.fieldName()) {
            case "id" -> setId(Long.valueOf(updatedValue.toString()));
            case "companyUuid", "uuid" -> setUuid(updatedValue.toString());
            case "companyGroup" -> setCompanyGroup(Long.valueOf(updatedValue.toString()));
        }
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setUuid(String uuid) {
        this.uuid = uuid;
    }

    private void setCompanyGroup(Long companyGroup) {
        this.companyGroup = companyGroup;
    }
}
