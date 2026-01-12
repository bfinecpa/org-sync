package org.orgsync.springsample.store.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.orgsync.core.dto.type.MemberType;

@Entity
@Table(name = "org_sync_member")
public class MemberEntity {

    @Id
    private Long id;

    private Long userId;

    private Long departmentId;

    private Long dutyCodeId;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    private int sortOrder;

    private int departmentOrder;

    protected MemberEntity() {
    }

    public MemberEntity(Long id, Long userId, Long departmentId, Long dutyCodeId, MemberType memberType,
                        int sortOrder, int departmentOrder) {
        this.id = id;
        this.userId = userId;
        this.departmentId = departmentId;
        this.dutyCodeId = dutyCodeId;
        this.memberType = memberType;
        this.sortOrder = sortOrder;
        this.departmentOrder = departmentOrder;
    }

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
}
