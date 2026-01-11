package org.orgsync.core.dto.snapshotDto;

import java.util.List;
import java.util.stream.Stream;
import org.orgsync.core.dto.domainDto.MemberDto;

public class TreeSnapshotDto {

    private Long id;

    private Boolean isRoot;

    private List<TreeDepartmentNodeSnapshotDto> childDepartments;

    private List<TreeMemberNodeSnapshotDto> childMembers;

    private Boolean isDeleted;

    public Stream<MemberDto> toMemberDto() {
        return childMembers.stream().map(dto -> dto.toMemberDto(this.id));
    }

    public Long getId() {
        return id;
    }

    public Boolean getRoot() {
        return isRoot;
    }

    public List<TreeDepartmentNodeSnapshotDto> getChildDepartments() {
        return childDepartments;
    }

    public List<TreeMemberNodeSnapshotDto> getChildMembers() {
        return childMembers;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }
}
