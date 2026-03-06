package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Dependent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DependentRepository extends JpaRepository<Dependent, UUID> {
    
    @Query("SELECT d FROM Dependent d WHERE d.employee.id = :employeeId AND d.isQualified = :isQualified")
    List<Dependent> findByEmployeeAndIsQualified(
        @Param("employeeId") UUID employeeId,
        @Param("isQualified") Boolean isQualified
    );
}