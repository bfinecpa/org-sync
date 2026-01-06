package org.orgsync.core.dto;

public class MemberDto implements Settable {

    private Long id;
    private Long user;
    private Long department;
    private Long dutyCode;
    private MemberType memberType;
    private int sortOrder;
    private int departmentOrder;

    public Long getId() {
        return id;
    }

    public Long getUser() {
        return user;
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

    public int getSortOrder() {
        return sortOrder;
    }

    public int getDepartmentOrder() {
        return departmentOrder;
    }

    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null || logInfoDto.updatedValue() == null) {
            return;
        }

        Object updatedValue = logInfoDto.updatedValue();
        switch (logInfoDto.fieldName()) {
            case "id" -> setId(Long.valueOf(updatedValue.toString()));
            case "user" -> setUser(Long.valueOf(updatedValue.toString()));
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

    private void setUser(Long user) {
        this.user = user;
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
