package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, UUID>, JpaSpecificationExecutor<AttendanceSummary> {

    List<AttendanceSummary> findByPayrollPeriodId(UUID payrollPeriodId);
    
    List<AttendanceSummary> findByPayrollPeriodIdAndStatus(UUID payrollPeriodId, AttendanceSummary.Status status);
    
    @Query("SELECT a FROM AttendanceSummary a WHERE a.payrollPeriod.id = :payrollPeriodId AND a.status = :status AND a.isDeleted = :isDeleted")
    List<AttendanceSummary> findByPayrollPeriodIdAndStatusAndIsDeleted(
        @Param("payrollPeriodId") UUID payrollPeriodId,
        @Param("status") AttendanceSummary.Status status,
        @Param("isDeleted") Boolean isDeleted
    );
    
    @Query("SELECT a FROM AttendanceSummary a WHERE a.payrollPeriod.id = :payrollPeriodId AND a.employee.id = :employeeId")
    Optional<AttendanceSummary> findByPayrollPeriodAndEmployee(
        @Param("payrollPeriodId") UUID payrollPeriodId,
        @Param("employeeId") UUID employeeId
    );

}