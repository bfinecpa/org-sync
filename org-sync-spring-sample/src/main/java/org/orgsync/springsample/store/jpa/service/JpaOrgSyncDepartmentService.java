package org.orgsync.springsample.store.jpa.service;

import java.util.List;
import java.util.stream.Collectors;
import org.orgsync.springsample.store.jpa.entity.DepartmentEntity;
import org.orgsync.springsample.store.jpa.repository.DepartmentRepository;
import org.orgsync.core.dto.domainDto.DepartmentDto;
import org.orgsync.core.service.OrgSyncDepartmentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncDepartmentService implements OrgSyncDepartmentService {

    private final DepartmentRepository departmentRepository;

    public JpaOrgSyncDepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public DepartmentDto findById(Long id) {
        return departmentRepository.findById(id)
            .map(this::toDto)
            .orElse(null);
    }

    @Override
    public void create(DepartmentDto departmentDto) {
        if (departmentDto == null) {
            return;
        }
        departmentRepository.save(toEntity(departmentDto));
    }

    @Override
    public void update(DepartmentDto departmentDto) {
        create(departmentDto);
    }

    @Override
    public void delete(Long departmentId) {
        departmentRepository.deleteById(departmentId);
    }

    @Override
    public List<DepartmentDto> findByCompanyId(Long id) {
        return departmentRepository.findByCompanyId(id).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void updateParentId(DepartmentDto departmentDto) {
        if (departmentDto == null) {
            return;
        }
        departmentRepository.findById(departmentDto.getId())
            .ifPresent(entity -> {
                entity.setParentId(departmentDto.getParentId());
                departmentRepository.save(entity);
            });
    }

    private DepartmentEntity toEntity(DepartmentDto dto) {
        return new DepartmentEntity(dto.getId(), dto.getCompanyId(), dto.getName(), dto.getParentId(),
            dto.getSortOrder(), dto.getCode(), dto.getAlias(), dto.getEmailId(), dto.getStatus(),
            dto.getDepartmentPath());
    }

    private DepartmentDto toDto(DepartmentEntity entity) {
        return new DepartmentDto(entity.getId(), entity.getCompanyId(), entity.getName(), entity.getParentId(),
            entity.getSortOrder(), entity.getCode(), entity.getAlias(), entity.getEmailId(), entity.getStatus(),
            entity.getDepartmentPath());
    }
}
