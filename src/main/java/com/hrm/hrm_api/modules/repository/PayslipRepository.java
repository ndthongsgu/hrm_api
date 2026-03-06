package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PayslipRepository extends JpaRepository<Payslip, UUID> {
    
    @Query("SELECT p FROM Payslip p WHERE p.payrollPeriod.id = :payrollPeriodId AND p.employee.id = :employeeId")
    Optional<Payslip> findByPayrollPeriodAndEmployee(
        @Param("payrollPeriodId") UUID payrollPeriodId,
        @Param("employeeId") UUID employeeId
    );
}