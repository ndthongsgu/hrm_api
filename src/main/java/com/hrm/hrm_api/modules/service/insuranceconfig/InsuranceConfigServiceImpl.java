package com.hrm.hrm_api.modules.service.insuranceconfig;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.InsuranceConfig;
import com.hrm.hrm_api.modules.repository.InsuranceConfigRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InsuranceConfigServiceImpl implements InsuranceConfigService {

    private final InsuranceConfigRepository repository;

    public InsuranceConfigServiceImpl(InsuranceConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<InsuranceConfig> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public InsuranceConfig findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(InsuranceConfig.class, id));
    }

    @Override
    @Transactional
    public InsuranceConfig create(InsuranceConfig entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public InsuranceConfig update(UUID id, InsuranceConfig entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(InsuranceConfig.class, id);
        }
        repository.deleteById(id);
    }
}