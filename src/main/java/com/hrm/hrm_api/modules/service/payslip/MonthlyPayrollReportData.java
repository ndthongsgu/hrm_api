package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.entity.Payslip;

import java.time.LocalDateTime;
import java.util.List;

public record MonthlyPayrollReportData(
        PayrollPeriod payrollPeriod,
        List<Payslip> payslips,
        String exportedBy,
        LocalDateTime exportedAt
) {
}
