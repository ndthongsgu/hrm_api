package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.modules.entity.Payslip;
import com.hrm.hrm_api.modules.service.CrudService;

import java.util.List;
import java.util.UUID;

public interface PayslipService extends CrudService<Payslip> {
	List<Payslip> generateForPayrollPeriod(UUID payrollPeriodId);
}