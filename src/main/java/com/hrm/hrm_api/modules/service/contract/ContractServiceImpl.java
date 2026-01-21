package com.hrm.hrm_api.modules.service.contract;

import com.hrm.hrm_api.modules.entity.Contract;
import com.hrm.hrm_api.modules.repository.ContractRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class ContractServiceImpl extends AbstractCrudService<Contract> implements ContractService {

    public ContractServiceImpl(ContractRepository repository) {
        super(repository, Contract.class);
    }
}