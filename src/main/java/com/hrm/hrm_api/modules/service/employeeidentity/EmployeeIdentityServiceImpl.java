package com.hrm.hrm_api.modules.service.employeeidentity;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.EmployeeIdentity;
import com.hrm.hrm_api.modules.repository.EmployeeIdentityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmployeeIdentityServiceImpl implements EmployeeIdentityService {

    private final EmployeeIdentityRepository repository;

    public EmployeeIdentityServiceImpl(EmployeeIdentityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<EmployeeIdentity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public EmployeeIdentity findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(EmployeeIdentity.class, id));
    }

    @Override
    @Transactional
    public EmployeeIdentity create(EmployeeIdentity entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public EmployeeIdentity update(UUID id, EmployeeIdentity entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(EmployeeIdentity.class, id);
        }
        repository.deleteById(id);
    }
}