package org.orgsync.core.dto;

import java.util.Map;

public class DepartmentDto {

    private Long id;
    private Long companyId;
    private String name;
    private Long parentId;
    private int sortOrder;
    private String code;
    private String alias;
    private String emailId;
    private String status;
    private String departmentPath;
    private Map<MultiLanguageType, MultiLanguageDto> multiLanguageDtoMap;

}
