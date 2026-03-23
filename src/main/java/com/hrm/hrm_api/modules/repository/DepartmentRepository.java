package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    @Modifying
    @Query("UPDATE Department d SET d.version = 0 WHERE d.id = :id AND d.version IS NULL")
    int normalizeNullVersion(@Param("id") UUID id);

    @Query(value = """
        SELECT * FROM departments d
        WHERE (:code IS NULL OR LOWER(d.code) LIKE LOWER(CONCAT('%', :code, '%')))
        AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:parentDepartmentId IS NULL OR d.parent_id = CAST(:parentDepartmentId AS UUID))
        """, nativeQuery = true)
    Page<Department> findByFilters(
            @Param("code") String code,
            @Param("name") String name,
            @Param("parentDepartmentId") String parentDepartmentId,
            Pageable pageable
    );
}