package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.SysSetting;
import com.hrm.hrm_api.modules.service.syssetting.SysSettingService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sys-settings")
public class SysSettingController {

    private final SysSettingService service;

    public SysSettingController(SysSettingService service) {
        this.service = service;
    }

    @GetMapping
    @RequirePermission(module = "SALARY", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PageResponse<SysSetting>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<SysSetting> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "SALARY", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<SysSetting>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping
    @RequirePermission(module = "SALARY", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<SysSetting>> create(@Valid @RequestBody SysSetting payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "SALARY", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<SysSetting>> update(@PathVariable java.util.UUID id, @Valid @RequestBody SysSetting payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "SALARY", action = PermissionDefinition.DELETE)
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}