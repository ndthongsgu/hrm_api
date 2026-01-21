package com.hrm.hrm_api.modules.service.employeeshift;

import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import com.hrm.hrm_api.modules.repository.EmployeeShiftAssignmentRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeShiftAssignmentServiceImpl extends AbstractCrudService<EmployeeShiftAssignment> implements EmployeeShiftAssignmentService {

    public EmployeeShiftAssignmentServiceImpl(EmployeeShiftAssignmentRepository repository) {
        super(repository, EmployeeShiftAssignment.class);
    }
}