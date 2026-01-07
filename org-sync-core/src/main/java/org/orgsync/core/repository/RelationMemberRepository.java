package org.orgsync.core.repository;

import org.orgsync.core.dto.MemberDto;

public interface RelationMemberRepository {

    void create(String companyUuid, MemberDto memberDto);

    void update(String companyUuid, Long memberId, String fieldName, Object updatedValue);

    void delete(String companyUuid, Long memberId);
}
