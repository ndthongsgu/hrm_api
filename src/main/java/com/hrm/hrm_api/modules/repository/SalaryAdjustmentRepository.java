package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SalaryAdjustmentRepository extends JpaRepository<SalaryAdjustment, UUID> {
	List<SalaryAdjustment> findByPayrollPeriodIdAndEmployeeId(UUID payrollPeriodId, UUID employeeId);
    
    @Query("SELECT sa FROM SalaryAdjustment sa WHERE sa.employee.id = :employeeId AND sa.payrollPeriod.id = :payrollPeriodId")
    List<SalaryAdjustment> findByEmployeeAndPayrollPeriod(
        @Param("employeeId") UUID employeeId,
        @Param("payrollPeriodId") UUID payrollPeriodId
    );
}