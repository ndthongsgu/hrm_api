package com.hrm.hrm_api.modules.service.role;

import com.hrm.hrm_api.modules.dto.request.RoleSearchRequest;
import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService extends CrudService<Role> {
    Page<Role> findByFilters(Pageable pageable, RoleSearchRequest.RoleFilterRequest filters);
}