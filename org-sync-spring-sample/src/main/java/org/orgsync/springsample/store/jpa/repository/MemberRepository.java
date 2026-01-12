package org.orgsync.springsample.store.jpa.repository;

import java.util.List;
import org.orgsync.springsample.store.jpa.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    @Query("select m from MemberEntity m "
        + "where m.userId in (select u.id from UserEntity u where u.companyId = :companyId) "
        + "or m.departmentId in (select d.id from DepartmentEntity d where d.companyId = :companyId)")
    List<MemberEntity> findByCompanyId(@Param("companyId") Long companyId);
}
