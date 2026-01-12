package org.orgsync.springsample.store.jpa.repository;

import java.util.List;
import org.orgsync.springsample.store.jpa.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

    List<DepartmentEntity> findByCompanyId(Long companyId);
}
