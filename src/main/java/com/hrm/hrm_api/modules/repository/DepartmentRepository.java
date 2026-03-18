package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    @Query(value = """
        SELECT * FROM departments d
        WHERE (:code IS NULL OR LOWER(d.code) LIKE LOWER(CONCAT('%', :code, '%')))
        AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:managerId IS NULL OR d.manager_id = CAST(:managerId AS UUID))
        AND (:parentDepartmentId IS NULL OR d.parent_department_id = CAST(:parentDepartmentId AS UUID))
        AND (:isActive IS NULL OR d.is_active = :isActive)
        """, nativeQuery = true)
    Page<Department> findByFilters(
            @Param("code") String code,
            @Param("name") String name,
            @Param("managerId") String managerId,
            @Param("parentDepartmentId") String parentDepartmentId,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}