package org.orgsync.springsample.store.jpa.service;

import java.util.List;
import java.util.stream.Collectors;
import org.orgsync.springsample.store.jpa.entity.MemberEntity;
import org.orgsync.springsample.store.jpa.repository.MemberRepository;
import org.orgsync.core.dto.domainDto.MemberDto;
import org.orgsync.core.service.OrgSyncMemberService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncMemberService implements OrgSyncMemberService {

    private final MemberRepository memberRepository;

    public JpaOrgSyncMemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public MemberDto findById(Long id) {
        return memberRepository.findById(id)
            .map(this::toDto)
            .orElse(null);
    }

    @Override
    public void create(MemberDto memberDto) {
        if (memberDto == null) {
            return;
        }
        memberRepository.save(toEntity(memberDto));
    }

    @Override
    public void update(MemberDto memberDto) {
        create(memberDto);
    }

    @Override
    public void delete(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    @Override
    public List<MemberDto> findByCompanyId(Long id) {
        return memberRepository.findByCompanyId(id).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private MemberEntity toEntity(MemberDto dto) {
        return new MemberEntity(dto.getId(), dto.getUserId(), dto.getDepartmentId(), dto.getDutyCodeId(),
            dto.getMemberType(), dto.getSortOrder(), dto.getDepartmentOrder());
    }

    private MemberDto toDto(MemberEntity entity) {
        return new MemberDto(entity.getId(), entity.getUserId(), entity.getDepartmentId(), entity.getDutyCodeId(),
            entity.getMemberType(), entity.getSortOrder(), entity.getDepartmentOrder());
    }
}
