package com.hrm.hrm_api.modules.service.payslip;

public record MonthlyPayrollReportExport(
        byte[] content,
        String asciiFilename,
        String utf8Filename
) {
}
