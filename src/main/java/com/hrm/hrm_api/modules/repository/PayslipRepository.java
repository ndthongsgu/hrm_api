package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PayslipRepository extends JpaRepository<Payslip, UUID>, JpaSpecificationExecutor<Payslip> {
	Optional<Payslip> findByPayrollPeriodIdAndEmployeeId(UUID payrollPeriodId, UUID employeeId);
    
    @Query("SELECT p FROM Payslip p WHERE p.payrollPeriod.id = :payrollPeriodId AND p.employee.id = :employeeId AND p.isDeleted = :isDeleted")
    Payslip findByPayrollPeriodAndEmployeeAndIsDeleted(
        @Param("payrollPeriodId") UUID payrollPeriodId,
        @Param("employeeId") UUID employeeId,
        @Param("isDeleted") boolean isDeleted
    );

    @Query("SELECT p FROM Payslip p WHERE p.payrollPeriod.id = :payrollPeriodId AND p.isDeleted = :isDeleted")
    List<Payslip> findByPayrollPeriodAndIsDeleted(
        @Param("payrollPeriodId") UUID payrollPeriodId,
        @Param("isDeleted") boolean isDeleted
    );

    @Query("SELECT p FROM Payslip p WHERE p.payrollPeriod.id = :payrollPeriodId AND p.employee.id = :employeeId AND p.status = 'APPROVED' AND p.isDeleted = :isDeleted")
    Payslip findByPayrollPeriodAndEmployeeAndApprovedStatus(
        @Param("payrollPeriodId") UUID payrollPeriodId,
        @Param("employeeId") UUID employeeId,
        @Param("isDeleted") boolean isDeleted
    );
}