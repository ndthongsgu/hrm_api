package com.hrm.hrm_api.modules.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrm.hrm_api.common.constants.Module;
import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.repository.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/admin/permissions")
public class PermissionController {

    private final RoleRepository roleRepository;
    private final ObjectMapper objectMapper;

    public PermissionController(RoleRepository roleRepository, ObjectMapper objectMapper) {
        this.roleRepository = roleRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    @RequirePermission(module = "PERMISSION", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<List<Module.ModuleDto>>> getAllPermissions() {
        List<Module.ModuleDto> modules = new Module().getModuleList();
        return ResponseEntity.ok(BaseResponse.ok(modules));
    }

    @GetMapping("/by-role/{roleId}")
    @RequirePermission(module = "PERMISSION", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<List<Module.ModuleDto>>> getPermissionsByRole(@PathVariable UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, roleId));

        Map<String, List<String>> rolePermissions = parsePermissions(role.getPermissionsJson());
        List<Module.ModuleDto> modules = applyRolePermissions(new Module().getModuleList(), rolePermissions);

        return ResponseEntity.ok(BaseResponse.ok(modules));
    }

    @PostMapping("/assign-to-role/{roleId}")
    @RequirePermission(module = "PERMISSION", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<Void>> assignPermissionsToRole(
            @PathVariable UUID roleId,
            @RequestBody(required = false) Map<String, List<String>> permissions) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, roleId));

        Map<String, List<String>> payload = permissions == null ? new HashMap<>() : normalizePermissions(permissions);

        try {
            role.setPermissionsJson(objectMapper.writeValueAsString(payload));
        } catch (IOException ex) {
            throw new IllegalArgumentException("Dữ liệu phân quyền không hợp lệ", ex);
        }

        roleRepository.save(role);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }

    private Map<String, List<String>> parsePermissions(String permissionsJson) {
        if (permissionsJson == null || permissionsJson.isBlank()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(permissionsJson, new TypeReference<Map<String, List<String>>>() {
            });
        } catch (IOException ex) {
            return new HashMap<>();
        }
    }

    private Map<String, List<String>> normalizePermissions(Map<String, List<String>> permissions) {
        Map<String, List<String>> normalized = new HashMap<>();

        permissions.forEach((moduleCode, actions) -> {
            if (moduleCode == null || moduleCode.isBlank() || actions == null) {
                return;
            }
            Set<String> uniqueActions = new HashSet<>();
            for (String action : actions) {
                if (action != null && !action.isBlank()) {
                    uniqueActions.add(action.trim().toUpperCase());
                }
            }
            normalized.put(moduleCode.trim().toUpperCase(), new ArrayList<>(uniqueActions));
        });

        return normalized;
    }

    private List<Module.ModuleDto> applyRolePermissions(
            List<Module.ModuleDto> modules,
            Map<String, List<String>> rolePermissions) {
        for (Module.ModuleDto module : modules) {
            List<String> actions = rolePermissions.getOrDefault(module.getCode(), List.of());
            Set<String> actionSet = new HashSet<>();
            for (String action : actions) {
                actionSet.add(action.toUpperCase());
            }

            module.setIsCanRead(actionSet.contains("READ"));
            module.setIsCanCreate(actionSet.contains("CREATE"));
            module.setIsCanUpdate(actionSet.contains("UPDATE"));
            module.setIsCanDelete(actionSet.contains("DELETE"));
            module.setIsCanActivate(actionSet.contains("ACTIVATE"));
            module.setIsCanDeactivate(actionSet.contains("DEACTIVATE"));
            module.setIsCanImport(actionSet.contains("IMPORT"));
            module.setIsCanExport(actionSet.contains("EXPORT"));
        }

        return modules;
    }
}
