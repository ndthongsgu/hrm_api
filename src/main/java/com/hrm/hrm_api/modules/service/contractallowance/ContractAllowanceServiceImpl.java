package com.hrm.hrm_api.modules.service.contractallowance;

import com.hrm.hrm_api.modules.entity.ContractAllowance;
import com.hrm.hrm_api.modules.repository.ContractAllowanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ContractAllowanceServiceImpl implements ContractAllowanceService {

    private final ContractAllowanceRepository contractAllowanceRepository;

    public ContractAllowanceServiceImpl(ContractAllowanceRepository repository) {
        this.contractAllowanceRepository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractAllowance> findAll(Pageable pageable) {
        return contractAllowanceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractAllowance findById(UUID id) {
        return contractAllowanceRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ContractAllowance create(ContractAllowance entity) {
        return contractAllowanceRepository.save(entity);
    }

    @Override
    @Transactional
    public ContractAllowance update(UUID id, ContractAllowance entity) {
        entity.setId(id);
        return contractAllowanceRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        contractAllowanceRepository.deleteById(id);
    }

    @Override
    public List<ContractAllowance> findByContractId(UUID contractId) {
        return contractAllowanceRepository.findByContractId(contractId);
    }
}   