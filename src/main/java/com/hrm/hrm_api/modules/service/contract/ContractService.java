package com.hrm.hrm_api.modules.service.contract;

import com.hrm.hrm_api.modules.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ContractService {
    Page<Contract> findAll(Pageable pageable);
    Contract findById(UUID id);
    Contract create(Contract entity);
    Contract update(UUID id, Contract entity);
    void delete(UUID id);
    Optional<Contract> findActiveByDate(UUID employeeId, LocalDate date);
    Optional<Contract> findActiveByPayrollPeriod(UUID employeeId, UUID payrollPeriodId);
}
