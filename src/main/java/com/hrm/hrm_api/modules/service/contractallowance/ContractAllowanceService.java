package com.hrm.hrm_api.modules.service.contractallowance;

import com.hrm.hrm_api.modules.dto.request.ContractAllowanceRequestDto;
import com.hrm.hrm_api.modules.dto.response.ContractAllowanceResponseDto;
import com.hrm.hrm_api.modules.dto.contractallowance.ContractAllowanceFilter;
import com.hrm.hrm_api.modules.entity.ContractAllowance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ContractAllowanceService {
    Page<ContractAllowance> search(Pageable pageable, ContractAllowanceFilter filter);
    Page<ContractAllowance> findAll(Pageable pageable);
    ContractAllowance findById(UUID id);
    ContractAllowanceResponseDto create(ContractAllowanceRequestDto request);
    ContractAllowanceResponseDto update(UUID id, ContractAllowanceRequestDto request);
    void delete(UUID id);
    List<ContractAllowance> findByContractId(UUID contractId);
}