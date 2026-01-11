package org.orgsync.core.service;

import java.util.List;
import org.orgsync.core.dto.domainDto.MemberDto;

public interface OrgSyncMemberService {

    default MemberDto findById(Long id) {
        return null;
    }

    default void create(MemberDto memberDto) {}

    default void update(MemberDto memberDto) {}

    default void delete(Long memberId) {}

    default  List<MemberDto> findByCompanyId(Long id) {
        return null;
    }
}
