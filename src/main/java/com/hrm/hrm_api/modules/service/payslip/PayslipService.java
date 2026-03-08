package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.modules.entity.Payslip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PayslipService {
	Page<Payslip> findAll(Pageable pageable);
	Payslip findById(UUID id);
	Payslip create(Payslip entity);
	Payslip update(UUID id, Payslip entity);
	void delete(UUID id);
	List<Payslip> generateForPayrollPeriod(UUID payrollPeriodId);
}