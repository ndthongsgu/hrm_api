package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.common.exception.PdfGenerationException;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.entity.Payslip;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonthlyPayrollReportPdfGeneratorTest {

    @Test
    void generate_returnsPdfDocumentWithVietnameseText() {
        MonthlyPayrollReportPdfGenerator generator =
                new MonthlyPayrollReportPdfGenerator("fonts/NotoSans-Regular.ttf");

        PayrollPeriod period = new PayrollPeriod();
        period.setName("Tháng 03/2026");
        period.setStartDate(LocalDate.of(2026, 3, 1));
        period.setEndDate(LocalDate.of(2026, 3, 31));

        Employee employee = new Employee();
        employee.setEmployeeCode("NV001");
        employee.setFullName("Nguyễn Văn A");

        Payslip payslip = new Payslip();
        payslip.setEmployee(employee);
        payslip.setGrossSalary(new BigDecimal("12000000"));
        payslip.setTaxableIncome(new BigDecimal("13500000"));
        payslip.setTotalDeductions(new BigDecimal("2500000"));
        payslip.setTaxAmount(new BigDecimal("350000"));
        payslip.setNetSalary(new BigDecimal("10650000"));

        MonthlyPayrollReportData reportData = new MonthlyPayrollReportData(
                period,
                List.of(payslip),
                "hr.manager",
                LocalDateTime.of(2026, 3, 31, 17, 30));

        byte[] pdf = generator.generate(reportData);

        assertTrue(pdf.length > 0);
        assertEquals("%PDF-", new String(Arrays.copyOfRange(pdf, 0, 5), StandardCharsets.US_ASCII));
    }

    @Test
    void generate_throwsSanitizedExceptionWhenFontIsMissing() {
        MonthlyPayrollReportPdfGenerator generator =
                new MonthlyPayrollReportPdfGenerator("fonts/missing-font.ttf");

        PdfGenerationException ex = assertThrows(
                PdfGenerationException.class,
                () -> generator.generate(new MonthlyPayrollReportData(
                        new PayrollPeriod(),
                        List.of(),
                        "hr.manager",
                        LocalDateTime.now())));

        assertEquals("Không thể tạo file PDF báo cáo lương", ex.getMessage());
    }
}
