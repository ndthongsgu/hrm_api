package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentCreateRequest;
import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentSearchRequest;
import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import com.hrm.hrm_api.modules.service.salaryadjustment.SalaryAdjustmentService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary-adjustments")
public class SalaryAdjustmentController {

    private final SalaryAdjustmentService service;

    public SalaryAdjustmentController(SalaryAdjustmentService service) {
        this.service = service;
    }

    @GetMapping
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PageResponse<SalaryAdjustment>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<SalaryAdjustment> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @PostMapping
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PaginationResponse<SalaryAdjustment>>> search(@RequestBody(required = false) SalaryAdjustmentSearchRequest request, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PaginationResponse<SalaryAdjustment> response = service.search(request, pageable);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<SalaryAdjustment>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping("/create")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<SalaryAdjustment>> create(@Valid @RequestBody SalaryAdjustmentCreateRequest request) {
        SalaryAdjustment created = service.create(request);
        return ResponseEntity.ok(BaseResponse.ok(created));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<SalaryAdjustment>> update(@PathVariable UUID id, @Valid @RequestBody SalaryAdjustmentCreateRequest request) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.DEACTIVATE)
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(BaseResponse.ok("Ngừng hoạt động salary adjustment thành công"));
    }

    /**
     * Kích hoạt lại salary adjustment (Soft delete recovery)
     */
    @PutMapping("/{id}/activate")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.ACTIVATE)
    public ResponseEntity<BaseResponse<String>> activate(@PathVariable UUID id) {
        service.activate(id);
        return ResponseEntity.ok(BaseResponse.ok("Kích hoạt lại salary adjustment thành công"));
    }

    /**
     * Duyệt điều chỉnh lương
     */
    @PostMapping("/{id}/approve")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> approve(@PathVariable UUID id) {
        service.approve(id);
        return ResponseEntity.ok(BaseResponse.ok("Duyệt điều chỉnh lương thành công"));
    }

}