package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.service.payrollperiod.PayrollPeriodService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payroll-periods")
public class PayrollPeriodController {

    private final PayrollPeriodService service;

    public PayrollPeriodController(PayrollPeriodService service) {
        this.service = service;
    }

    @GetMapping
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PageResponse<PayrollPeriod>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<PayrollPeriod> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PayrollPeriod>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<PayrollPeriod>> create(@Valid @RequestBody PayrollPeriod payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<PayrollPeriod>> update(@PathVariable java.util.UUID id, @Valid @RequestBody PayrollPeriod payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.DELETE)
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}