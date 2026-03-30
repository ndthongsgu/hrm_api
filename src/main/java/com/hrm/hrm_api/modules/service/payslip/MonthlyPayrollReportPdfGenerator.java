package com.hrm.hrm_api.modules.service.payslip;

import org.springframework.stereotype.Component;

@Component
public class MonthlyPayrollReportPdfGenerator {

    MonthlyPayrollReportPdfGenerator() {
    }

    MonthlyPayrollReportPdfGenerator(String fontClasspathLocation) {
    }

    public byte[] generate(MonthlyPayrollReportData reportData) {
        throw new UnsupportedOperationException("Implement in Task 2");
    }
}
