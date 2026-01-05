package org.orgsync.core.dto;

public class MultiLanguageDto {
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
}


