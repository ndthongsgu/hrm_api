package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.common.exception.PdfGenerationException;
import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.common.security.SecurityUtil;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.entity.Payslip;
import com.hrm.hrm_api.modules.repository.AttendanceSummaryRepository;
import com.hrm.hrm_api.modules.repository.ContractAllowanceRepository;
import com.hrm.hrm_api.modules.repository.ContractRepository;
import com.hrm.hrm_api.modules.repository.DependentRepository;
import com.hrm.hrm_api.modules.repository.InsuranceConfigRepository;
import com.hrm.hrm_api.modules.repository.PayrollPeriodRepository;
import com.hrm.hrm_api.modules.repository.PayslipRepository;
import com.hrm.hrm_api.modules.repository.SalaryAdjustmentRepository;
import com.hrm.hrm_api.modules.repository.SysSettingRepository;
import com.hrm.hrm_api.modules.repository.TaxBracketRepository;
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayslipServiceImplTest {

    @Mock private PayslipRepository payslipRepository;
    @Mock private PayrollPeriodRepository payrollPeriodRepository;
    @Mock private AttendanceSummaryRepository attendanceSummaryRepository;
    @Mock private ContractRepository contractRepository;
    @Mock private ContractAllowanceRepository contractAllowanceRepository;
    @Mock private SalaryAdjustmentRepository salaryAdjustmentRepository;
    @Mock private DependentRepository dependentRepository;
    @Mock private SysSettingRepository sysSettingRepository;
    @Mock private TaxBracketRepository taxBracketRepository;
    @Mock private InsuranceConfigRepository insuranceConfigRepository;
    @Mock private ActionLogUtil actionLogUtil;
    @Mock private SecurityUtil securityUtil;
    @Mock private MonthlyPayrollReportPdfGenerator monthlyPayrollReportPdfGenerator;

    @InjectMocks
    private PayslipServiceImpl service;

    @Test
    void exportMonthlyReport_throwsNotFoundWhenPayrollPeriodMissing() {
        UUID payrollPeriodId = UUID.randomUUID();

        when(payrollPeriodRepository.findById(payrollPeriodId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.exportMonthlyReport(payrollPeriodId));
    }

    @Test
    void exportMonthlyReport_throwsWhenPayrollPeriodNotApproved() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setStatus(PayrollPeriod.Status.PENDING);
        period.setIsDeleted(false);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.exportMonthlyReport(period.getId()));

        assertTrue(ex.getMessage().contains("không thể xuất"));
    }

    @Test
    void exportMonthlyReport_throwsWhenNoApprovedPayslipsExist() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setName("Tháng 03/2026");
        period.setStatus(PayrollPeriod.Status.APPROVED);
        period.setIsDeleted(false);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));
        when(payslipRepository
                .findByPayrollPeriodIdAndStatusAndIsDeletedOrderByEmployee_EmployeeCodeAscEmployee_FullNameAsc(
                        period.getId(), Payslip.Status.APPROVED, false))
                .thenReturn(List.of());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.exportMonthlyReport(period.getId()));

        assertEquals("Không có phiếu lương đã duyệt để xuất báo cáo", ex.getMessage());
    }

    @Test
    void exportMonthlyReport_throwsWhenPayrollPeriodSoftDeleted() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setStatus(PayrollPeriod.Status.APPROVED);
        period.setIsDeleted(true);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.exportMonthlyReport(period.getId()));

        assertTrue(ex.getMessage().contains("không thể xuất"));
    }

    @Test
    void exportMonthlyReport_propagatesSanitizedPdfGenerationException() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setName("Tháng 03/2026");
        period.setStatus(PayrollPeriod.Status.APPROVED);
        period.setIsDeleted(false);

        Payslip payslip = new Payslip();
        Employee employee = new Employee();
        employee.setEmployeeCode("NV001");
        employee.setFullName("Nguyễn Văn A");
        payslip.setEmployee(employee);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));
        when(payslipRepository
                .findByPayrollPeriodIdAndStatusAndIsDeletedOrderByEmployee_EmployeeCodeAscEmployee_FullNameAsc(
                        period.getId(), Payslip.Status.APPROVED, false))
                .thenReturn(List.of(payslip));
        when(monthlyPayrollReportPdfGenerator.generate(any(MonthlyPayrollReportData.class)))
                .thenThrow(new PdfGenerationException("Không thể tạo file PDF báo cáo lương", new RuntimeException("font missing")));

        PdfGenerationException ex = assertThrows(
                PdfGenerationException.class,
                () -> service.exportMonthlyReport(period.getId()));

        assertEquals("Không thể tạo file PDF báo cáo lương", ex.getMessage());
    }

    @Test
    void exportMonthlyReport_returnsPdfBytesAndFilenames() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setName("Tháng 03/2026");
        period.setStatus(PayrollPeriod.Status.APPROVED);
        period.setIsDeleted(false);

        Payslip payslip = new Payslip();
        Employee employee = new Employee();
        employee.setEmployeeCode("NV001");
        employee.setFullName("Nguyễn Văn A");
        payslip.setEmployee(employee);
        payslip.setStatus(Payslip.Status.APPROVED);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));
        when(payslipRepository
                .findByPayrollPeriodIdAndStatusAndIsDeletedOrderByEmployee_EmployeeCodeAscEmployee_FullNameAsc(
                        period.getId(), Payslip.Status.APPROVED, false))
                .thenReturn(List.of(payslip));
        when(securityUtil.getCurrentUsername()).thenReturn("hr.manager");
        when(monthlyPayrollReportPdfGenerator.generate(any(MonthlyPayrollReportData.class)))
                .thenReturn("%PDF-demo".getBytes(StandardCharsets.UTF_8));

        MonthlyPayrollReportExport result = service.exportMonthlyReport(period.getId());

        assertArrayEquals("%PDF-demo".getBytes(StandardCharsets.UTF_8), result.content());
        assertEquals("bao-cao-luong-thang-thang-03-2026.pdf", result.asciiFilename());
        assertTrue(result.utf8Filename().contains("Tháng 03/2026"));
    }
}
