package com.hrm.hrm_api.modules.service.role;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.request.RoleSearchRequest;
import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository repository) {
        this.roleRepository = repository;
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    @Override
    public Role findById(UUID id) {
        return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Role.class, id));
    }

    @Override
    @Transactional
    public Role create(Role entity) {
        return roleRepository.save(entity);
    }

    @Override
    @Transactional
    public Role update(UUID id, Role entity) {
        Role existing = findById(id);

        existing.setName(entity.getName());
        existing.setDescription(entity.getDescription());
        existing.setPermissionsJson(entity.getPermissionsJson());

        return roleRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException(Role.class, id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public Page<Role> findByFilters(Pageable pageable, RoleSearchRequest.RoleFilterRequest filters) {
        if (filters == null) {
            return roleRepository.findAll(pageable);
        }
        return roleRepository.findByFilters(
                filters.getName(),
                filters.getIsActive(),
                pageable
        );
    }
}