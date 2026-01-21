package com.hrm.hrm_api.modules.service.contractallowance;

import com.hrm.hrm_api.modules.entity.ContractAllowance;
import com.hrm.hrm_api.modules.repository.ContractAllowanceRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class ContractAllowanceServiceImpl extends AbstractCrudService<ContractAllowance> implements ContractAllowanceService {

    public ContractAllowanceServiceImpl(ContractAllowanceRepository repository) {
        super(repository, ContractAllowance.class);
    }
}