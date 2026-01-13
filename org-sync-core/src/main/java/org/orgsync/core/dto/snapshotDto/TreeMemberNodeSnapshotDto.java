package org.orgsync.core.dto.snapshotDto;

import org.orgsync.core.dto.domainDto.MemberDto;
import org.orgsync.core.dto.type.MemberType;

public record TreeMemberNodeSnapshotDto(
    Long memberId,
    Long userId,
    Long companyId,
    String dutyName,
    Long dutyId,
    int sortOrder,
    int departmentOrder,
    Boolean isMaster,
    MemberType memberType
) {

    public MemberDto toMemberDto(Long departmentId) {
        return new MemberDto(memberId, userId, departmentId, dutyId, memberType, sortOrder, departmentOrder);
    }
}
