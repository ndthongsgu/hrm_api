package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.Payslip;
import com.hrm.hrm_api.modules.service.payslip.MonthlyPayrollReportExport;
import com.hrm.hrm_api.modules.service.payslip.PayslipService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    private final PayslipService service;

    public PayslipController(PayslipService service) {
        this.service = service;
    }

    @PostMapping("")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PageResponse<Payslip>>> search(
            @RequestBody(required = false) java.util.Map<String, Object> filters,
            @PageableDefault(size = 20) Pageable pageable) {
        if (filters == null) {
            filters = new java.util.HashMap<>();
        }
        Page<Payslip> page = service.search(filters, pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<Payslip>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    /**
     * Tạo phiếu lương cho một nhân viên
     */
    @PostMapping("/generate/{payrollPeriodId}/employee/{employeeId}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<Payslip>> generateForEmployee(@PathVariable UUID payrollPeriodId, @PathVariable UUID employeeId) {
        return ResponseEntity.ok(BaseResponse.ok(service.generateForEmployee(payrollPeriodId, employeeId)));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<Payslip>> update(@PathVariable java.util.UUID id, @Valid @RequestBody Payslip payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.DEACTIVATE)
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable java.util.UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(BaseResponse.ok("Ngừng hoạt động phiếu lương thành công"));
    }

    /**
     * Kích hoạt lại phiếu lương (Soft delete recovery)
     */
    @PutMapping("/{id}/activate")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.ACTIVATE)
    public ResponseEntity<BaseResponse<String>> activate(@PathVariable java.util.UUID id) {
        service.activate(id);
        return ResponseEntity.ok(BaseResponse.ok("Kích hoạt lại phiếu lương thành công"));
    }

    /**
     * Duyệt phiếu lương
     */
    @PostMapping("/{id}/approve")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> approve(@PathVariable java.util.UUID id) {
        service.approve(id);
        return ResponseEntity.ok(BaseResponse.ok("Duyệt phiếu lương thành công"));
    }

    @GetMapping(value = "/export/monthly-report/{payrollPeriodId}", produces = {MediaType.APPLICATION_PDF_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.EXPORT)
        public ResponseEntity<?> exportMonthlyReport(@PathVariable UUID payrollPeriodId) {
        try {
            MonthlyPayrollReportExport export = service.exportMonthlyReport(payrollPeriodId);

            String disposition = "attachment; filename=\"" + export.asciiFilename() + "\"; filename*=UTF-8''"
                + UriUtils.encode(export.utf8Filename(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(export.content());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BaseResponse.error("Thao tác không hợp lệ: " + ex.getMessage()));
        }
    }

    // Lấy phiếu lương của một nhân viên trong kỳ lương cụ thể (API cho app mobile)
    @GetMapping("/employee/{employeeId}/payroll-period/{payrollPeriodId}")
    public ResponseEntity<BaseResponse<Payslip>> getByEmployeeAndPayrollPeriod(@PathVariable UUID employeeId, @PathVariable UUID payrollPeriodId) {
        return ResponseEntity.ok(BaseResponse.ok(service.findByEmployeeAndPayrollPeriod(employeeId, payrollPeriodId)));
    }
}
