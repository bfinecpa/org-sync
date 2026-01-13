package org.orgsync.core.dto.deltaDto;

import org.orgsync.core.Constants;
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
            case "id" -> setId(updatedValue.toString());
            case "user" -> setUserId(updatedValue.toString());
            case "department" -> setDepartment(updatedValue.toString());
            case "dutyCode" -> setDutyCode(updatedValue.toString());
            case "memberType" -> setMemberType(MemberType.valueOf(updatedValue.toString()));
            case "sortOrder" -> setSortOrder(updatedValue.toString());
            case "departmentOrder" -> setDepartmentOrder(updatedValue.toString());
        }
    }

    private void setId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "id is null in MemberDeltaDto");
        }else {
            this.id = Long.parseLong(id);
        }
    }

    private void setUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "userId is null in MemberDeltaDto");
        }else {
            this.userId = Long.parseLong(userId);
        }
    }

    private void setDepartment(String department) {
        if (department == null || department.isEmpty()) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "department is null in MemberDeltaDto");
        }else {
            this.department = Long.parseLong(department);
        }
    }

    private void setDutyCode(String dutyCode) {
        if (dutyCode == null || dutyCode.isEmpty()) {
            this.dutyCode = null;
        }else {
            this.dutyCode = Long.parseLong(dutyCode);
        }
    }

    private void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    private void setSortOrder(String sortOrder) {
        if (sortOrder == null || sortOrder.isEmpty()) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "sortOrder is null in MemberDeltaDto");
        }
        this.sortOrder = Integer.parseInt(sortOrder);
    }

    private void setDepartmentOrder(String departmentOrder) {
        if (departmentOrder == null || departmentOrder.isEmpty()) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "departmentOrder is null in MemberDeltaDto");
        }
        this.departmentOrder = Integer.parseInt(departmentOrder);
    }
}
