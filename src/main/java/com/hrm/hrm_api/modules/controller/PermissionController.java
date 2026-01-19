package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.entity.Permission;
import com.hrm.hrm_api.modules.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @GetMapping("/{id}")
    public Permission getPermissionById(@PathVariable Long id) {
        return permissionService.getPermissionById(id).orElse(null);
    }

    @PostMapping
    public Permission createPermission(@RequestBody Permission permission) {
        return permissionService.savePermission(permission);
    }

    @PutMapping("/{id}")
    public Permission updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        return permissionService.savePermission(permission);
    }

    @DeleteMapping("/{id}")
    public void deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
    }
}
