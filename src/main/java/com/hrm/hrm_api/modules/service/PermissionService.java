package com.hrm.hrm_api.modules.service;

import com.hrm.hrm_api.modules.entity.Permission;
import com.hrm.hrm_api.modules.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    public Permission savePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }
}
