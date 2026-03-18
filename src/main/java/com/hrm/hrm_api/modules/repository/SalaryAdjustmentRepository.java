package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SalaryAdjustmentRepository extends JpaRepository<SalaryAdjustment, UUID>, JpaSpecificationExecutor<SalaryAdjustment> {
	List<SalaryAdjustment> findByPayrollPeriodIdAndEmployeeId(UUID payrollPeriodId, UUID employeeId);
	
	List<SalaryAdjustment> findByPayrollPeriodIdAndEmployeeIdAndStatus(UUID payrollPeriodId, UUID employeeId, SalaryAdjustment.Status status);
    
    @Query("SELECT sa FROM SalaryAdjustment sa WHERE sa.payrollPeriod.id = :payrollPeriodId AND sa.employee.id = :employeeId AND sa.status = :status AND sa.isDeleted = :isDeleted")
    List<SalaryAdjustment> findByPayrollPeriodIdAndEmployeeIdAndStatusAndIsDeleted(
        @Param("payrollPeriodId") UUID payrollPeriodId,
        @Param("employeeId") UUID employeeId,
        @Param("status") SalaryAdjustment.Status status,
        @Param("isDeleted") Boolean isDeleted
    );
    
    @Query("SELECT sa FROM SalaryAdjustment sa WHERE sa.employee.id = :employeeId AND sa.payrollPeriod.id = :payrollPeriodId")
    List<SalaryAdjustment> findByEmployeeAndPayrollPeriod(
        @Param("employeeId") UUID employeeId,
        @Param("payrollPeriodId") UUID payrollPeriodId
    );

    @Query("SELECT sa FROM SalaryAdjustment sa WHERE sa.payrollPeriod.id = :payrollPeriodId AND sa.isDeleted = :isDeleted")
    List<SalaryAdjustment> findByPayrollPeriodAndIsDeleted(
        @Param("payrollPeriodId") UUID payrollPeriodId,
        @Param("isDeleted") Boolean isDeleted
    );
}