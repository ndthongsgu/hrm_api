package com.hrm.hrm_api.modules.service.department;

import com.hrm.hrm_api.modules.dto.request.DepartmentSearchRequest;
import com.hrm.hrm_api.modules.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DepartmentService {
    Page<Department> findAll(Pageable pageable);
    Department findById(UUID id);
    Department create(Department entity);
    Department update(UUID id, Department entity);
    void delete(UUID id);
    Page<Department> findByFilters(Pageable pageable, DepartmentSearchRequest.DepartmentFilterRequest filters);
}
