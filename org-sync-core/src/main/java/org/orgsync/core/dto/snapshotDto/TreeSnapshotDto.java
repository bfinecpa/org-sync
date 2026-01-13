package org.orgsync.core.dto.snapshotDto;

import java.util.List;
import java.util.stream.Stream;
import org.orgsync.core.dto.domainDto.MemberDto;

public record TreeSnapshotDto(
    Long id,
    Boolean isRoot,
    List<TreeDepartmentNodeSnapshotDto> childDepartments,
    List<TreeMemberNodeSnapshotDto> childMembers,
    Boolean isDeleted
) {

    public Stream<MemberDto> toMemberDto() {
        return childMembers.stream().map(dto -> dto.toMemberDto(this.id));
    }
}
