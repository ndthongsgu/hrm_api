package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.modules.entity.Payslip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PayslipService {
	Page<Payslip> search(java.util.Map<String, Object> filters, Pageable pageable);
	Payslip findById(UUID id);
	Payslip update(UUID id, Payslip entity);
	void deactivate(UUID id);
	void activate(UUID id);
	void approve(UUID id);
	void unApprove(UUID id);
	String bulkApprove(List<UUID> ids);
	String bulkUnApprove(List<UUID> ids);
	Payslip generateForEmployee(UUID payrollPeriodId, UUID employeeId);
	String generateBulkForEmployees(UUID payrollPeriodId, List<UUID> employeeIds);
	Payslip findByEmployeeAndPayrollPeriod(UUID employeeId, UUID payrollPeriodId);
	MonthlyPayrollReportExport exportMonthlyReport(UUID payrollPeriodId);
	List<UUID> getIdsByFilter(java.util.Map<String, Object> filters);
}
