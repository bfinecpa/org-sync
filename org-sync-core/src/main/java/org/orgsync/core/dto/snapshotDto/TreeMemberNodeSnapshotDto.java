package org.orgsync.core.dto.snapshotDto;

import org.orgsync.core.dto.domainDto.MemberDto;
import org.orgsync.core.dto.type.MemberType;

public class TreeMemberNodeSnapshotDto {

    private Long memberId;

    private Long userId;

    private Long companyId;

    private String dutyName;

    private Long dutyId;

    private int sortOrder;

    private int departmentOrder; // 유저가 멀티포지션인 경우의 순서

    private Boolean isMaster;

    private MemberType memberType;


    public MemberDto toMemberDto(Long departmentId) {
        return new MemberDto(memberId, userId, departmentId, dutyId, memberType, sortOrder, departmentOrder);
    }

}
