package org.orgsync.core.dto.domainDto;

import org.orgsync.core.dto.deltaDto.MemberDeltaDto;
import org.orgsync.core.dto.type.MemberType;
import org.orgsync.core.dto.deltaDto.Settable;

public class MemberDto implements Settable {

    private Long id;
    private Long userId;
    private Long departmentId;
    private Long dutyCodeId;
    private MemberType memberType;
    private int sortOrder;
    private int departmentOrder;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Long getDutyCodeId() {
        return dutyCodeId;
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

    public MemberDto(Long domainId) {
        this.id = domainId;
    }

    public MemberDto(Long id, Long userId, Long departmentId, Long dutyCodeId, MemberType memberType, int sortOrder,
        int departmentOrder) {
        this.id = id;
        this.userId = userId;
        this.departmentId = departmentId;
        this.dutyCodeId = dutyCodeId;
        this.memberType = memberType;
        this.sortOrder = sortOrder;
        this.departmentOrder = departmentOrder;
    }

    public void update(Long userId, Long departmentId, Long dutyCodeId, MemberType memberType, Integer sortOrder,
        Integer departmentOrder) {
        if (userId != null) {
            this.userId = userId;
        }

        if (departmentId != null) {
            this.departmentId = departmentId;
        }

        if (dutyCodeId != null) {
            this.dutyCodeId = dutyCodeId;
        }

        if (memberType != null) {
            this.memberType = memberType;
        }

        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }

        if (departmentOrder != null) {
            this.departmentOrder = departmentOrder;
        }
    }
}
