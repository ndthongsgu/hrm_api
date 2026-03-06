package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.service.role.RoleService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @GetMapping
    @RequirePermission(module = "ROLE", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PageResponse<Role>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<Role> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "ROLE", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<Role>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping
    @RequirePermission(module = "ROLE", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<Role>> create(@Valid @RequestBody Role payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "ROLE", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<Role>> update(@PathVariable UUID id, @Valid @RequestBody Role payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "ROLE", action = PermissionDefinition.DELETE)
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}