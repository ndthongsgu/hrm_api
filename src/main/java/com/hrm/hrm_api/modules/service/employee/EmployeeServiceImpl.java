package com.hrm.hrm_api.modules.service.employee;

import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends AbstractCrudService<Employee> implements EmployeeService {

    public EmployeeServiceImpl(EmployeeRepository repository) {
        super(repository, Employee.class);
    }
}
