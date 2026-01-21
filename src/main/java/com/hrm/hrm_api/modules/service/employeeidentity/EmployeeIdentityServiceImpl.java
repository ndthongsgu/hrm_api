package com.hrm.hrm_api.modules.service.employeeidentity;

import com.hrm.hrm_api.modules.entity.EmployeeIdentity;
import com.hrm.hrm_api.modules.repository.EmployeeIdentityRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeIdentityServiceImpl extends AbstractCrudService<EmployeeIdentity> implements EmployeeIdentityService {

    public EmployeeIdentityServiceImpl(EmployeeIdentityRepository repository) {
        super(repository, EmployeeIdentity.class);
    }
}