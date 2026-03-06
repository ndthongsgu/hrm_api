package com.hrm.hrm_api.modules.service.department;

import com.hrm.hrm_api.modules.dto.request.DepartmentSearchRequest;
import com.hrm.hrm_api.modules.entity.Department;
import com.hrm.hrm_api.modules.repository.DepartmentRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends AbstractCrudService<Department> implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository repository) {
        super(repository, Department.class);
        this.departmentRepository = repository;
    }

    @Override
    public Page<Department> findByFilters(Pageable pageable, DepartmentSearchRequest.DepartmentFilterRequest filters) {
        if (filters == null) {
            return departmentRepository.findAll(pageable);
        }
        return departmentRepository.findByFilters(
                filters.getCode(),
                filters.getName(),
                filters.getManagerId(),
                filters.getParentDepartmentId(),
                filters.getIsActive(),
                pageable
        );
    }
}
