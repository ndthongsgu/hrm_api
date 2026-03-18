package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.PayrollPeriodSearchRequest;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.service.payrollperiod.PayrollPeriodService;
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

    public PayrollPeriodController(PayrollPeriodService service) {
        this.service = service;
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
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<PayrollPeriod>> create(@Valid @RequestBody PayrollPeriod payload) {
        PayrollPeriod created = service.create(payload);
        return ResponseEntity.ok(BaseResponse.ok(created));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<PayrollPeriod>> update(@PathVariable UUID id, @Valid @RequestBody PayrollPeriod payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}/deActivate")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.DELETE)
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(BaseResponse.ok("Ngừng hoạt động kỳ lương thành công"));
    }

    /**
     * Kích hoạt lại kỳ lương (Soft delete recovery)
     */
    @PutMapping("/{id}/activate")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.ACTIVATE)
    public ResponseEntity<BaseResponse<String>> activate(@PathVariable UUID id) {
        service.activate(id);
        return ResponseEntity.ok(BaseResponse.ok("Kích hoạt lại kỳ lương thành công"));
    }


    /**
     * Duyệt kỳ lương
     */
    @PostMapping("/{id}/approve")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> approve(@PathVariable UUID id) {
        service.approve(id);
        return ResponseEntity.ok(BaseResponse.ok("Duyệt kỳ lương thành công"));
    }

    /**
     * Hủy Duyệt kỳ lương
     */
    @PostMapping("/{id}/unApprove")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> unApprove(@PathVariable UUID id) {
        service.unApprove(id);
        return ResponseEntity.ok(BaseResponse.ok("Hủy duyệt kỳ lương thành công"));
    }

    /**
     * Selectbox kỳ lương cho app mobile (Chỉ lấy nhưng kỳ lương đã duyệt)
     */
    @GetMapping("/mobile-select-options")
    public ResponseEntity<BaseResponse<List<SelectOptionDto>>> getMobileSelectOptions() {
        List<SelectOptionDto> options = service.getMobileSelectOptions();
        return ResponseEntity.ok(BaseResponse.ok(options));
    }
}