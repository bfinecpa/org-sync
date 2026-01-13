package org.orgsync.core.dto.deltaDto;

import org.orgsync.core.Constants;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.type.OrganizationCodeType;

public class OrganizationCodeDeltaDto implements Settable {
    private Long id;
    private String code;
    private OrganizationCodeType type;
    private String name;
    private Integer sortOrder;
    private String multiLanguageDtoMap;

    public OrganizationCodeDeltaDto(Long domainId) {
        this.id = domainId;
    }

    public Long getId() {
        return id;
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public String getMultiLanguageDtoMap() {
        return multiLanguageDtoMap;
    }


    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "logInfoDto is null in OrganizationCodeDto");
        }

        String fieldName = logInfoDto.fieldName();
        Object updatedValue = logInfoDto.updatedValue();

        switch (fieldName) {
            case "id" -> setId(updatedValue.toString());
            case "code" -> setCode(updatedValue.toString());
            case "type" -> setType(OrganizationCodeType.valueOf(updatedValue.toString()));
            case "name" -> setName(updatedValue.toString());
            case "sortOrder" -> setSortOrder(Integer.valueOf(updatedValue.toString()));
            case "multiLanguageDtoMap" -> setMultiLanguageDtoMap(updatedValue.toString());
        }

    }

    private void setId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "id is null in OrganizationCodeDto");
        }
        this.id = Long.parseLong(id);
    }

    private void setCode(String code) {
        this.code = code;
    }

    private void setType(OrganizationCodeType type) {
        this.type = type;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    private void setMultiLanguageDtoMap(String multiLanguageDtoMap) {
        this.multiLanguageDtoMap = multiLanguageDtoMap;
    }
}
