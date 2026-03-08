package com.hrm.hrm_api.modules.service.contract;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.Contract;
import com.hrm.hrm_api.modules.repository.ContractRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository repository;

    public ContractServiceImpl(ContractRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<Contract> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Contract findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Contract.class, id));
    }

    @Override
    @Transactional
    public Contract create(Contract entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Contract update(UUID id, Contract entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(Contract.class, id);
        }
        repository.deleteById(id);
    }
}