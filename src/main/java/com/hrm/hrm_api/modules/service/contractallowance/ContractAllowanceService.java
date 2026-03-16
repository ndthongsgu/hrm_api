package com.hrm.hrm_api.modules.service.contractallowance;

import com.hrm.hrm_api.modules.entity.ContractAllowance;
import com.hrm.hrm_api.modules.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ContractAllowanceService {
    Page<ContractAllowance> findAll(Pageable pageable);
    ContractAllowance findById(UUID id);
    ContractAllowance create(ContractAllowance entity);
    ContractAllowance update(UUID id, ContractAllowance entity);
    void delete(UUID id);
    List<ContractAllowance> findByContractId(UUID contractId);
}