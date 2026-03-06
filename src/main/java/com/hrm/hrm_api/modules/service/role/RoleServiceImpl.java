package com.hrm.hrm_api.modules.service.role;

import com.hrm.hrm_api.modules.dto.request.RoleSearchRequest;
import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.repository.RoleRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends AbstractCrudService<Role> implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository repository) {
        super(repository, Role.class);
        this.roleRepository = repository;
    }

    @Override
    public Page<Role> findByFilters(Pageable pageable, RoleSearchRequest.RoleFilterRequest filters) {
        if (filters == null) {
            return roleRepository.findAll(pageable);
        }
        return roleRepository.findByFilters(
                filters.getCode(),
                filters.getName(),
                filters.getIsActive(),
                pageable
        );
    }
}