package com.hrm.hrm_api.modules.service.department;

import com.hrm.hrm_api.modules.entity.Department;
import com.hrm.hrm_api.modules.repository.DepartmentRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends AbstractCrudService<Department> implements DepartmentService {

    public DepartmentServiceImpl(DepartmentRepository repository) {
        super(repository, Department.class);
    }
}
