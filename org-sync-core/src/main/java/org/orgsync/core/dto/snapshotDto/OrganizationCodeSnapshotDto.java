package org.orgsync.core.dto.snapshotDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.domainDto.OrganizationCodeDto;
import org.orgsync.core.dto.type.OrganizationCodeType;
import org.orgsync.core.dto.type.TargetDomain;

public class OrganizationCodeSnapshotDto {

    private Long id;
    private String code;
    private OrganizationCodeType type;
    private String name;
    private int sortOrder;
    Map<MultiLanguageType, String> multiLanguageMap;


    public OrganizationCodeSnapshotDto(Long id, String code, OrganizationCodeType type, String name, int sortOrder,
        Map<MultiLanguageType, String> multiLanguageMap) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.name = name;
        this.sortOrder = sortOrder;
        this.multiLanguageMap = multiLanguageMap;
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

    public int getSortOrder() {
        return sortOrder;
    }

    public Map<MultiLanguageType, String> getMultiLanguageMap() {
        return multiLanguageMap;
    }



    public OrganizationCodeDto toOrganizationCodeDto() {
        //TODO: DTO 생성
        return null;
    }

    public List<MultiLanguageDto> toMultiLanguageDtos() {
        List<MultiLanguageDto> multiLanguageDtos = new ArrayList<>();
        for (Entry<MultiLanguageType, String> entry : multiLanguageMap.entrySet()) {
            multiLanguageDtos.add(new MultiLanguageDto(this.id, TargetDomain.ORGANIZATION_CODE, entry.getKey(), entry.getValue()));
        }
        return multiLanguageDtos;
    }
}
