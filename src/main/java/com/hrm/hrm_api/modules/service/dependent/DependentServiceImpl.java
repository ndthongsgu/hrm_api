package com.hrm.hrm_api.modules.service.dependent;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.Dependent;
import com.hrm.hrm_api.modules.repository.DependentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DependentServiceImpl implements DependentService {

    private final DependentRepository repository;

    public DependentServiceImpl(DependentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<Dependent> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Dependent findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Dependent.class, id));
    }

    @Override
    @Transactional
    public Dependent create(Dependent entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Dependent update(UUID id, Dependent entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(Dependent.class, id);
        }
        repository.deleteById(id);
    }
}
