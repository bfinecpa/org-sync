package org.orgsync.core.dto;

import java.util.Map;
import org.orgsync.core.Constants;
import org.orgsync.core.util.MultiLanguageUtils;

public class DepartmentDto implements Settable {

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

    public DepartmentDto(Long companyId) {
        this.companyId = companyId;
    }

    public Long getId() {
        return id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public String getCode() {
        return code;
    }

    public String getAlias() {
        return alias;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getStatus() {
        return status;
    }

    public String getDepartmentPath() {
        return departmentPath;
    }

    public Map<MultiLanguageType, MultiLanguageDto> getMultiLanguageDtoMap() {
        return multiLanguageDtoMap;
    }

    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null) {
            throw new IllegalArgumentException(Constants.ERROR_PREFIX + "logInfoDto is null in DepartmentDto");
        }

        Object updatedValue = logInfoDto.updatedValue();
        if (updatedValue == null) {
            return;
        }

        switch (logInfoDto.fieldName()) {
            case "id" -> setId(Long.valueOf(updatedValue.toString()));
            case "companyId", "company_id" -> setCompanyId(Long.valueOf(updatedValue.toString()));
            case "name" -> setName(updatedValue.toString());
            case "parent", "parentId" -> setParentId(Long.valueOf(updatedValue.toString()));
            case "sort_order", "sortOrder" -> setSortOrder(Integer.parseInt(updatedValue.toString()));
            case "code" -> setCode(updatedValue.toString());
            case "alias" -> setAlias(updatedValue.toString());
            case "emailId" -> setEmailId(updatedValue.toString());
            case "status" -> setStatus(updatedValue.toString());
            case "departmentPath" -> setDepartmentPath(updatedValue.toString());
            case "multiLanguageDtoMap", "multiLanguageMap" -> setMultiLanguageDtoMap(
                MultiLanguageUtils.parseJson(logInfoDto.domainId(), TargetDomain.DEPARTMENT,
                    updatedValue.toString()));
        }
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    private void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    private void setCode(String code) {
        this.code = code;
    }

    private void setAlias(String alias) {
        this.alias = alias;
    }

    private void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    private void setDepartmentPath(String departmentPath) {
        this.departmentPath = departmentPath;
    }

    private void setMultiLanguageDtoMap(Map<MultiLanguageType, MultiLanguageDto> multiLanguageDtoMap) {
        this.multiLanguageDtoMap = multiLanguageDtoMap;
    }
}
