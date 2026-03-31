package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.exception.PdfGenerationException;
import com.hrm.hrm_api.common.security.UserDetailsImpl;
import com.hrm.hrm_api.modules.service.payslip.MonthlyPayrollReportExport;
import com.hrm.hrm_api.modules.service.payslip.PayslipService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
class PayslipControllerExportTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayslipService payslipService;

    @Test
    void exportMonthlyReport_returnsPdfWhenUserHasExportPermission() throws Exception {
        UUID payrollPeriodId = UUID.randomUUID();
        MonthlyPayrollReportExport export = new MonthlyPayrollReportExport(
                "%PDF-demo".getBytes(StandardCharsets.UTF_8),
                "bao-cao-luong-thang-thang-03-2026.pdf",
                "bao-cao-luong-thang-Tháng 03/2026.pdf");

        when(payslipService.exportMonthlyReport(payrollPeriodId)).thenReturn(export);
        authenticateAs("PAYROLL_EXPORT");

        mockMvc.perform(get("/api/payslips/export/monthly-report/{payrollPeriodId}", payrollPeriodId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, allOf(
                        containsString("attachment;"),
                        containsString("filename=\"bao-cao-luong-thang-thang-03-2026.pdf\""),
                        containsString("filename*=UTF-8''"))));
    }

    @Test
    void exportMonthlyReport_returnsForbiddenWhenPermissionMissing() throws Exception {
        UUID payrollPeriodId = UUID.randomUUID();
        authenticateAs("PAYROLL_READ");

        mockMvc.perform(get("/api/payslips/export/monthly-report/{payrollPeriodId}", payrollPeriodId))
                .andExpect(status().isForbidden());
    }

        @Test
        void exportMonthlyReport_returnsBadRequestWhenNoApprovedPayslipsExist() throws Exception {
                UUID payrollPeriodId = UUID.randomUUID();
                authenticateAs("PAYROLL_EXPORT");
                when(payslipService.exportMonthlyReport(payrollPeriodId))
                                .thenThrow(new IllegalStateException("Không có phiếu lương đã duyệt để xuất báo cáo"));

                mockMvc.perform(get("/api/payslips/export/monthly-report/{payrollPeriodId}", payrollPeriodId)
                                                .accept(MediaType.APPLICATION_PDF))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Thao tác không hợp lệ: Không có phiếu lương đã duyệt để xuất báo cáo"));
        }

    @Test
    void exportMonthlyReport_returnsSanitizedServerErrorWhenPdfGenerationFails() throws Exception {
        UUID payrollPeriodId = UUID.randomUUID();
        authenticateAs("PAYROLL_EXPORT");
        when(payslipService.exportMonthlyReport(payrollPeriodId))
                .thenThrow(new PdfGenerationException("Không thể tạo file PDF báo cáo lương", new RuntimeException("font missing")));

        mockMvc.perform(get("/api/payslips/export/monthly-report/{payrollPeriodId}", payrollPeriodId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Không thể tạo file PDF báo cáo lương"));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String authority) {
        UserDetailsImpl principal = new UserDetailsImpl(
                UUID.randomUUID(),
                "hr.manager",
                "hr.manager",
                "secret",
                List.of(new SimpleGrantedAuthority(authority)),
                false);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "secret",
                principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
