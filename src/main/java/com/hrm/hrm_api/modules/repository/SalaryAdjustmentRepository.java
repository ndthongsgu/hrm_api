package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalaryAdjustmentRepository extends JpaRepository<SalaryAdjustment, UUID> {
	List<SalaryAdjustment> findByPayrollPeriodIdAndEmployeeId(UUID payrollPeriodId, UUID employeeId);
}