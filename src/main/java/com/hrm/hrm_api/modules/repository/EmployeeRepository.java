package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByEmail(String email);

    @Query(value = """
        SELECT * FROM employees e 
        WHERE (:fullName IS NULL OR LOWER(e.full_name) LIKE LOWER('%' || :fullName || '%'))
        AND (:employeeCode IS NULL OR LOWER(e.employee_code) LIKE LOWER('%' || :employeeCode || '%'))
        AND (:isDeleted IS NULL OR e.is_deleted = :isDeleted)
        """, nativeQuery = true)
    Page<Employee> findByFilters(
        @Param("fullName") String fullName,
        @Param("employeeCode") String employeeCode,
        @Param("isDeleted") Boolean isDeleted,
        Pageable pageable
    );
}
