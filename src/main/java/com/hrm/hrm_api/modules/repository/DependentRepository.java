package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Dependent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DependentRepository extends JpaRepository<Dependent, UUID> {
	long countByEmployeeIdAndIsQualifiedTrue(UUID employeeId);

    @Query("SELECT d FROM Dependent d WHERE d.employee.id = :employeeId AND d.isDeleted = false")
    Page<Dependent> findByEmployeeIdAndFilters(
            @Param("employeeId") UUID employeeId,
            @Param("fullName") String fullName,
            @Param("relationship") String relationship,
            Pageable pageable
    );
    
    @Query("SELECT COUNT(d) FROM Dependent d WHERE d.employee.id = :employeeId AND d.isQualified = true AND d.isDeleted = false")
    long countByEmployeeIdAndIsQualifiedTrueAndIsDeletedFalse(@Param("employeeId") UUID employeeId);
    
    @Query("SELECT d FROM Dependent d WHERE d.employee.id = :employeeId AND d.isQualified = :isQualified")
    List<Dependent> findByEmployeeAndIsQualified(
        @Param("employeeId") UUID employeeId,
        @Param("isQualified") Boolean isQualified
    );
}