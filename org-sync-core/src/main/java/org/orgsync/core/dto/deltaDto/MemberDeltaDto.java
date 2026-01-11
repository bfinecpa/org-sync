package org.orgsync.core.dto.deltaDto;

import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.type.MemberType;

public class MemberDeltaDto implements Settable {
    private Long id;
    private Long userId;
    private Long department;
    private Long dutyCode;
    private MemberType memberType;
    private Integer sortOrder;
    private Integer departmentOrder;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getDepartment() {
        return department;
    }

    public Long getDutyCode() {
        return dutyCode;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public Integer getDepartmentOrder() {
        return departmentOrder;
    }

    public MemberDeltaDto(Long domainId) {
        this.id = domainId;
    }

    public MemberDeltaDto(Long id, Long userId, Long department, Long dutyCode, MemberType memberType, int sortOrder,
        int departmentOrder) {
        this.id = id;
        this.userId = userId;
        this.department = department;
        this.dutyCode = dutyCode;
        this.memberType = memberType;
        this.sortOrder = sortOrder;
        this.departmentOrder = departmentOrder;
    }

    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null || logInfoDto.updatedValue() == null) {
            return;
        }

        Object updatedValue = logInfoDto.updatedValue();
        switch (logInfoDto.fieldName()) {
            case "id" -> setId(Long.valueOf(updatedValue.toString()));
            case "user" -> setUserId(Long.valueOf(updatedValue.toString()));
            case "department" -> setDepartment(Long.valueOf(updatedValue.toString()));
            case "dutyCode" -> setDutyCode(Long.valueOf(updatedValue.toString()));
            case "memberType" -> setMemberType(MemberType.valueOf(updatedValue.toString()));
            case "sortOrder" -> setSortOrder(Integer.parseInt(updatedValue.toString()));
            case "departmentOrder" -> setDepartmentOrder(Integer.parseInt(updatedValue.toString()));
        }
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setUserId(Long userId) {
        this.userId = userId;
    }

    private void setDepartment(Long department) {
        this.department = department;
    }

    private void setDutyCode(Long dutyCode) {
        this.dutyCode = dutyCode;
    }

    private void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    private void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    private void setDepartmentOrder(int departmentOrder) {
        this.departmentOrder = departmentOrder;
    }
}
