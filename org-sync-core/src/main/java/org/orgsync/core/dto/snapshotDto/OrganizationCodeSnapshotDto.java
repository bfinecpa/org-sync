package org.orgsync.core.dto.snapshotDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.domainDto.OrganizationCodeDto;
import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.type.OrganizationCodeType;
import org.orgsync.core.dto.type.TargetDomain;

public record OrganizationCodeSnapshotDto(
    Long id,
    String code,
    OrganizationCodeType type,
    String name,
    int sortOrder,
    Map<MultiLanguageType, String> multiLanguageMap
) {

    public OrganizationCodeDto toOrganizationCodeDto(Long companyId) {
        return new OrganizationCodeDto(
            id,
            companyId,
            code,
            type,
            name,
            sortOrder
        );
    }

    public List<MultiLanguageDto> toMultiLanguageDtos() {
        List<MultiLanguageDto> multiLanguageDtos = new ArrayList<>();
        for (Entry<MultiLanguageType, String> entry : multiLanguageMap.entrySet()) {
            multiLanguageDtos.add(
                new MultiLanguageDto(this.id, TargetDomain.ORGANIZATION_CODE, entry.getKey(), entry.getValue()));
        }
        return multiLanguageDtos;
    }
}
