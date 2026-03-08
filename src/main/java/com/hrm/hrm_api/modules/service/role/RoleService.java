package com.hrm.hrm_api.modules.service.role;

import com.hrm.hrm_api.modules.dto.request.RoleSearchRequest;
import com.hrm.hrm_api.modules.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RoleService {
    Page<Role> findAll(Pageable pageable);
    Role findById(UUID id);
    Role create(Role entity);
    Role update(UUID id, Role entity);
    void delete(UUID id);
    Page<Role> findByFilters(Pageable pageable, RoleSearchRequest.RoleFilterRequest filters);
}