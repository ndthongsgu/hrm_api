package com.hrm.hrm_api.modules.service.allowancetype;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.AllowanceType;
import com.hrm.hrm_api.modules.repository.AllowanceTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AllowanceTypeServiceImpl implements AllowanceTypeService {

    private final AllowanceTypeRepository repository;

    public AllowanceTypeServiceImpl(AllowanceTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<AllowanceType> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public AllowanceType findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(AllowanceType.class, id));
    }

    @Override
    @Transactional
    public AllowanceType create(AllowanceType entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public AllowanceType update(UUID id, AllowanceType entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(AllowanceType.class, id);
        }
        repository.deleteById(id);
    }
}