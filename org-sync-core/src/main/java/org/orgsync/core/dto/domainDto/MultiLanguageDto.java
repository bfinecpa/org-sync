package org.orgsync.core.dto.domainDto;

import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.type.TargetDomain;

public class MultiLanguageDto{
    private Long id;
    private TargetDomain targetDomain;
    private MultiLanguageType multiLanguageType;
    private String value;

    public MultiLanguageDto(Long id, TargetDomain targetDomain, MultiLanguageType multiLanguageType, String value) {
        this.id = id;
        this.targetDomain = targetDomain;
        this.multiLanguageType = multiLanguageType;
        this.value = value;
    }

    public MultiLanguageDto(Long id, TargetDomain targetDomain) {
        this.id = id;
        this.targetDomain = targetDomain;
    }


    public Long getId() {
        return id;
    }

    public TargetDomain getTargetDomain() {
        return targetDomain;
    }

    public MultiLanguageType getMultiLanguageType() {
        return multiLanguageType;
    }

    public String getValue() {
        return value;
    }

}


