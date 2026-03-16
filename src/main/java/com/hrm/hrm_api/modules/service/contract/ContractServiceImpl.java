package com.hrm.hrm_api.modules.service.contract;

import com.hrm.hrm_api.modules.entity.Contract;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.repository.ContractRepository;
import com.hrm.hrm_api.modules.repository.PayrollPeriodRepository;
import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository repository;
    private final PayrollPeriodRepository payrollPeriodRepository;

    public ContractServiceImpl(ContractRepository repository, PayrollPeriodRepository payrollPeriodRepository) {
        this.repository = repository;
        this.payrollPeriodRepository = payrollPeriodRepository;
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

    @Override
    public Optional<Contract> findActiveByDate(UUID employeeId, LocalDate date) {
        LocalDate effectiveDate = date == null ? LocalDate.now() : date;
        List<Contract> contracts = repository.findByEmployeeIdAndStartDateLessThanEqualOrderByStartDateDesc(employeeId, effectiveDate);
        return contracts.stream()
                .filter(this::isActiveStatus)
                .filter(c -> c.getEndDate() == null || !c.getEndDate().isBefore(effectiveDate))
                .findFirst();
    }

    @Override
    public Optional<Contract> findActiveByPayrollPeriod(UUID employeeId, UUID payrollPeriodId) {
        PayrollPeriod period = payrollPeriodRepository.findById(payrollPeriodId)
                .orElseThrow(() -> new ResourceNotFoundException(PayrollPeriod.class, payrollPeriodId));
        LocalDate effectiveDate = period.getEndDate() != null ? period.getEndDate() : LocalDate.now();
        return findActiveByDate(employeeId, effectiveDate);
    }

    private boolean isActiveStatus(Contract contract) {
        return contract.getStatus() == null || "ACTIVE".equalsIgnoreCase(contract.getStatus());
    }
}