package com.hrm.hrm_api.modules.service.contractallowance;

import com.hrm.hrm_api.modules.entity.ContractAllowance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ContractAllowanceService {
    Page<ContractAllowance> findAll(Pageable pageable);
    ContractAllowance findById(UUID id);
    ContractAllowance create(ContractAllowance entity);
    ContractAllowance update(UUID id, ContractAllowance entity);
    void delete(UUID id);
}