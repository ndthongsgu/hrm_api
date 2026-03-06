package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.Payslip;
import com.hrm.hrm_api.modules.service.payslip.PayslipService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    private final PayslipService service;

    public PayslipController(PayslipService service) {
        this.service = service;
    }

    @GetMapping
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PageResponse<Payslip>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<Payslip> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<Payslip>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<Payslip>> create(@Valid @RequestBody Payslip payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    @PostMapping("/generate/{payrollPeriodId}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<List<Payslip>>> generate(@PathVariable UUID payrollPeriodId) {
        return ResponseEntity.ok(BaseResponse.ok(service.generateForPayrollPeriod(payrollPeriodId)));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<Payslip>> update(@PathVariable java.util.UUID id, @Valid @RequestBody Payslip payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.DELETE)
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}