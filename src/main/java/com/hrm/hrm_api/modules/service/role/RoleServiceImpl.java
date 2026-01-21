package com.hrm.hrm_api.modules.service.role;

import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.repository.RoleRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends AbstractCrudService<Role> implements RoleService {

    public RoleServiceImpl(RoleRepository repository) {
        super(repository, Role.class);
    }
}