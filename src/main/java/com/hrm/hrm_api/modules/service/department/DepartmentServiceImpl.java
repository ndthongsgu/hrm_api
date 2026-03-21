package com.hrm.hrm_api.modules.service.department;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.request.DepartmentSearchRequest;
import com.hrm.hrm_api.modules.entity.Department;
import com.hrm.hrm_api.modules.repository.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository repository) {
        this.departmentRepository = repository;
    }

    @Override
    public Page<Department> findAll(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    @Override
    public Department findById(UUID id) {
        return departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Department.class, id));
    }

    @Override
    @Transactional
    public Department create(Department entity) {
        return departmentRepository.save(entity);
    }

    @Override
    @Transactional
    public Department update(UUID id, Department entity) {
        findById(id);
        entity.setId(id);
        return departmentRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException(Department.class, id);
        }
        departmentRepository.deleteById(id);
    }

    @Override
    public Page<Department> findByFilters(Pageable pageable, DepartmentSearchRequest.DepartmentFilterRequest filters) {
        if (filters == null) {
            return departmentRepository.findAll(pageable);
        }
        return departmentRepository.findByFilters(
                filters.getCode(),
                filters.getName(),
            filters.getParentDepartmentId(),
                pageable
        );
    }
}
