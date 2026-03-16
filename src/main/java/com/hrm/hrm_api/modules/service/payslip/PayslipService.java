package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.modules.entity.Payslip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PayslipService {
	Page<Payslip> findAll(Pageable pageable);
	Page<Payslip> search(java.util.Map<String, Object> filters, Pageable pageable);
	Payslip findById(UUID id);
	Payslip create(Payslip entity);
	Payslip update(UUID id, Payslip entity);
	void delete(UUID id);
	void deactivate(UUID id);
	void activate(UUID id);
	void approve(UUID id);
	List<Payslip> generateForPayrollPeriod(UUID payrollPeriodId);
	Payslip generateForEmployee(UUID payrollPeriodId, UUID employeeId);
}