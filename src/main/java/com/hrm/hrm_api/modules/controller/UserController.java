package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.dto.request.UserRoleAssignRequest;
import com.hrm.hrm_api.modules.dto.response.UserResponse;
import com.hrm.hrm_api.modules.entity.User;
import com.hrm.hrm_api.modules.mapper.UserMapper;
import com.hrm.hrm_api.modules.service.user.UserService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @Transactional(readOnly = true)
    @RequirePermission(module = "PERMISSION", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PageResponse<UserResponse>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<User> page = service.findAll(pageable);
        PageResponse<UserResponse> response = PageResponse.of(
                page.getContent().stream()
                        .map(UserMapper::toResponse)
                        .collect(Collectors.toList()),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @RequirePermission(module = "PERMISSION", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<UserResponse>> getById(@PathVariable UUID id) {
        User user = service.findById(id);
        return ResponseEntity.ok(BaseResponse.ok(UserMapper.toResponse(user)));
    }

    @PostMapping
    @Transactional
    @RequirePermission(module = "PERMISSION", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<UserResponse>> create(@Valid @RequestBody User payload) {
        User created = service.create(payload);
        return ResponseEntity.ok(BaseResponse.ok(UserMapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @Transactional
    @RequirePermission(module = "PERMISSION", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<UserResponse>> update(@PathVariable UUID id, @Valid @RequestBody User payload) {
        User updated = service.update(id, payload);
        return ResponseEntity.ok(BaseResponse.ok(UserMapper.toResponse(updated)));
    }

    @PutMapping("/{id}/roles")
    @Transactional
    @RequirePermission(module = "PERMISSION", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<UserResponse>> assignRoles(@PathVariable UUID id, @RequestBody UserRoleAssignRequest payload) {
        User updated = service.assignRoles(id, payload == null ? null : payload.getRoleIds());
        return ResponseEntity.ok(BaseResponse.ok(UserMapper.toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @RequirePermission(module = "PERMISSION", action = PermissionDefinition.DELETE)
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}