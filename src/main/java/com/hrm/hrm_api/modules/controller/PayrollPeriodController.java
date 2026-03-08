package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.common.security.SecurityUtil;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.payrollperiod.PayrollPeriodFilter;
import com.hrm.hrm_api.modules.dto.request.PayrollPeriodSearchRequest;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.service.payrollperiod.PayrollPeriodService;
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payroll-periods")
public class PayrollPeriodController {

    private final PayrollPeriodService service;
    private final ActionLogUtil actionLogUtil;
    private final SecurityUtil securityUtil;

    public PayrollPeriodController(PayrollPeriodService service, ActionLogUtil actionLogUtil, SecurityUtil securityUtil) {
        this.service = service;
        this.actionLogUtil = actionLogUtil;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PaginationResponse<PayrollPeriod>>> search(@RequestBody(required = false) PayrollPeriodSearchRequest request, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PaginationResponse<PayrollPeriod> response = service.search(request, pageable);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/select-options")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<List<SelectOptionDto>>> getSelectOptions() {
        List<SelectOptionDto> options = service.getSelectOptions();
        return ResponseEntity.ok(BaseResponse.ok(options));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PayrollPeriod>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<PayrollPeriod>> create(@Valid @RequestBody PayrollPeriod payload) {
        PayrollPeriod created = service.create(payload);
        
        // Log action
        actionLogUtil.logCreate(
            created.getId(),
            "PAYROLL_PERIOD",
            "Tạo kỳ lương: " + created.getName(),
            securityUtil.getCurrentUsername()
        );
        
        return ResponseEntity.ok(BaseResponse.ok(created));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<PayrollPeriod>> update(@PathVariable UUID id, @Valid @RequestBody PayrollPeriod payload) {
        PayrollPeriod updated = service.update(id, payload);
        
        // Log action
        actionLogUtil.logUpdate(
            id,
            "PAYROLL_PERIOD",
            "Cập nhật kỳ lương: " + updated.getName(),
            securityUtil.getCurrentUsername()
        );
        
        return ResponseEntity.ok(BaseResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.DELETE)
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable UUID id) {
        PayrollPeriod period = service.findById(id);
        service.deactivate(id);
        
        // Log action
        actionLogUtil.logDeactivate(
            id,
            "PAYROLL_PERIOD",
            "Ngừng hoạt động kỳ lương: " + period.getName(),
            securityUtil.getCurrentUsername()
        );
        
        return ResponseEntity.ok(BaseResponse.ok("Ngừng hoạt động kỳ lương thành công"));
    }

    /**
     * Kích hoạt lại kỳ lương (Soft delete recovery)
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<BaseResponse<String>> activate(@PathVariable UUID id) {
        PayrollPeriod period = service.findById(id);
        service.activate(id);
        
        // Log action
        actionLogUtil.logActivate(
            id,
            "PAYROLL_PERIOD",
            "Kích hoạt lại kỳ lương: " + period.getName(),
            securityUtil.getCurrentUsername()
        );
        
        return ResponseEntity.ok(BaseResponse.ok("Kích hoạt lại kỳ lương thành công"));
    }

    /**
     * Bước 1: Tự động tổng hợp công
     */
    @PostMapping("/{id}/summarize-attendance")
    public ResponseEntity<BaseResponse<String>> summarizeAttendance(@PathVariable UUID id) {
        service.summarizeAttendance(id);
        return ResponseEntity.ok(BaseResponse.ok("Tổng hợp công thành công"));
    }

    /**
     * Bước 3: Tính lương
     */
    @PostMapping("/{id}/calculate-payslips")
    public ResponseEntity<BaseResponse<String>> calculatePayslips(@PathVariable UUID id) {
        service.calculatePayslips(id);
        return ResponseEntity.ok(BaseResponse.ok("Tính lương thành công"));
    }

    /**
     * Bước 4: Chốt kỳ lương (Lock)
     */
    @PostMapping("/{id}/lock")
    public ResponseEntity<BaseResponse<String>> lockPeriod(@PathVariable UUID id) {
        service.lockPeriod(id);
        return ResponseEntity.ok(BaseResponse.ok("Chốt kỳ lương thành công"));
    }
}