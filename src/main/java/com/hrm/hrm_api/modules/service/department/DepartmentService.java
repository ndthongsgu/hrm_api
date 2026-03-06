package com.hrm.hrm_api.modules.service.department;

import com.hrm.hrm_api.modules.dto.request.DepartmentSearchRequest;
import com.hrm.hrm_api.modules.entity.Department;
import com.hrm.hrm_api.modules.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService extends CrudService<Department> {
    Page<Department> findByFilters(Pageable pageable, DepartmentSearchRequest.DepartmentFilterRequest filters);
}
