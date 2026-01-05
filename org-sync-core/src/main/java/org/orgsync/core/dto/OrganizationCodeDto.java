package org.orgsync.core.dto;

import java.util.Map;
import org.orgsync.core.Constants;
import org.orgsync.core.util.MultiLanguageUtils;

public class OrganizationCodeDto implements Settable{

    private Long id;
    private String code;
    private OrganizationCodeType type;
    private String name;
    private int sortOrder;
    private Map<MultiLanguageType, MultiLanguageDto> multiLanguageDtoMap;

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

    public int getSortOrder() {
        return sortOrder;
    }

    public Map<MultiLanguageType, MultiLanguageDto> getMultiLanguageDtoMap() {
        return multiLanguageDtoMap;
    }

    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null) {
            throw new IllegalArgumentException(Constants.ERROR_PREFIX + "logInfoDto is null in OrganizationCodeDto");
        }

        String fieldName = logInfoDto.fieldName();
        Object updatedValue = logInfoDto.updatedValue();

        switch (fieldName) {
            case "id" -> setId(Long.valueOf(updatedValue.toString()));
            case "code" -> setCode(updatedValue.toString());
            case "type" -> setType(OrganizationCodeType.valueOf(updatedValue.toString()));
            case "name" -> setName(updatedValue.toString());
            case "sortOrder" -> setSortOrder(Integer.valueOf(updatedValue.toString()));
            case "multiLanguageDtoMap" -> {
                Map<MultiLanguageType, MultiLanguageDto> multiLanguageDtoMap = MultiLanguageUtils.parseJson(
                    logInfoDto.domainId(), TargetDomain.ORGANIZATION_CODE,
                    updatedValue.toString());
                setMultiLanguageDtoMap(multiLanguageDtoMap);
            }
        }

    }

    private void setId(Long id) {
        this.id = id;
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

    private void setMultiLanguageDtoMap(
        Map<MultiLanguageType, MultiLanguageDto> multiLanguageDtoMap) {
        this.multiLanguageDtoMap = multiLanguageDtoMap;
    }
}
