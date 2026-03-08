package com.hrm.hrm_api.modules.service.contractallowance;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.ContractAllowance;
import com.hrm.hrm_api.modules.repository.ContractAllowanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ContractAllowanceServiceImpl implements ContractAllowanceService {

    private final ContractAllowanceRepository repository;

    public ContractAllowanceServiceImpl(ContractAllowanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<ContractAllowance> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public ContractAllowance findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ContractAllowance.class, id));
    }

    @Override
    @Transactional
    public ContractAllowance create(ContractAllowance entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public ContractAllowance update(UUID id, ContractAllowance entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(ContractAllowance.class, id);
        }
        repository.deleteById(id);
    }
}