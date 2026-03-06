package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PayslipRepository extends JpaRepository<Payslip, UUID> {
	Optional<Payslip> findByPayrollPeriodIdAndEmployeeId(UUID payrollPeriodId, UUID employeeId);
}