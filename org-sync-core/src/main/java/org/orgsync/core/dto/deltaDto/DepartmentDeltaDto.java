package org.orgsync.core.dto.deltaDto;

import org.orgsync.core.Constants;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.type.DepartmentStatus;


public class DepartmentDeltaDto implements Settable {

    private Long id;
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private String code;
    private String alias;
    private String emailId;
    private DepartmentStatus status;
    private String departmentPath;
    private String multiLanguageDtoMap;

    public DepartmentDeltaDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }

    public Integer getSortOrder() {
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

    public DepartmentStatus getStatus() {
        return status;
    }

    public String getDepartmentPath() {
        return departmentPath;
    }

    public String getMultiLanguageDtoMap() {
        return multiLanguageDtoMap;
    }


    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "logInfoDto is null in DepartmentDto");
        }

        Object updatedValue = logInfoDto.updatedValue();
        if (updatedValue == null) {
            return;
        }

        switch (logInfoDto.fieldName()) {
            case "id" -> setId(Long.valueOf(updatedValue.toString()));
            case "name" -> setName(updatedValue.toString());
            case "parent", "parentId" -> setParentId(Long.valueOf(updatedValue.toString()));
            case "sort_order", "sortOrder" -> setSortOrder(Integer.parseInt(updatedValue.toString()));
            case "code" -> setCode(updatedValue.toString());
            case "alias" -> setAlias(updatedValue.toString());
            case "emailId" -> setEmailId(updatedValue.toString());
            case "status" -> setStatus(updatedValue.toString());
            case "departmentPath" -> setDepartmentPath(updatedValue.toString());
            case "multiLanguageMap" -> setMultiLanguageDtoMap(updatedValue.toString());
        }
    }

    private void setId(Long id) {
        this.id = id;
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
        this.status = DepartmentStatus.valueOf(status);
    }

    private void setDepartmentPath(String departmentPath) {
        this.departmentPath = departmentPath;
    }

    private void setMultiLanguageDtoMap(String multiLanguageDtoMap) {
        this.multiLanguageDtoMap = multiLanguageDtoMap;
    }

}
