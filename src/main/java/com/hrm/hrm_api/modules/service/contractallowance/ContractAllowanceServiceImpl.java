package com.hrm.hrm_api.modules.service.contractallowance;

import com.hrm.hrm_api.modules.entity.ContractAllowance;
import com.hrm.hrm_api.modules.repository.ContractAllowanceRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ContractAllowanceServiceImpl extends AbstractCrudService<ContractAllowance> implements ContractAllowanceService {

    private final ContractAllowanceRepository contractAllowanceRepository;

    public ContractAllowanceServiceImpl(ContractAllowanceRepository repository) {
        super(repository, ContractAllowance.class);
        this.contractAllowanceRepository = repository;
    }

    @Override
    public List<ContractAllowance> findByContractId(UUID contractId) {
        return contractAllowanceRepository.findByContractId(contractId);
    }
}