package com.hrm.hrm_api.modules.service.contract;

import com.hrm.hrm_api.modules.dto.request.ContractRequestDto;
import com.hrm.hrm_api.modules.dto.contract.ContractFilter;
import com.hrm.hrm_api.modules.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ContractService {
    Page<Contract> search(Pageable pageable, ContractFilter filter);
    Page<Contract> findAll(Pageable pageable);
    Contract findById(UUID id);
    Contract create(ContractRequestDto payload);
    Contract update(UUID id, ContractRequestDto payload);
    void delete(UUID id);
    Optional<Contract> findActiveByDate(UUID employeeId, LocalDate date);
    Optional<Contract> findActiveByPayrollPeriod(UUID employeeId, UUID payrollPeriodId);
}
