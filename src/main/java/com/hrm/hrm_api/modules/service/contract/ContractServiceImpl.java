package com.hrm.hrm_api.modules.service.contract;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.contract.ContractFilter;
import com.hrm.hrm_api.modules.dto.request.ContractRequestDto;
import com.hrm.hrm_api.modules.entity.Contract;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.entity.Position;
import com.hrm.hrm_api.modules.repository.ContractRepository;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.repository.PayrollPeriodRepository;
import com.hrm.hrm_api.modules.repository.PositionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;

    public ContractServiceImpl(
            ContractRepository repository,
            PayrollPeriodRepository payrollPeriodRepository,
            EmployeeRepository employeeRepository,
            PositionRepository positionRepository
    ) {
        this.repository = repository;
        this.payrollPeriodRepository = payrollPeriodRepository;
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public Page<Contract> search(Pageable pageable, ContractFilter filter) {
        return repository.findAll(buildSpec(filter), pageable);
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
    public Contract create(ContractRequestDto payload) {
        Contract entity = new Contract();
        applyPayload(entity, payload);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public Contract update(UUID id, ContractRequestDto payload) {
        Contract entity = findById(id);
        applyPayload(entity, payload);
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

    private Specification<Contract> buildSpec(ContractFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter != null) {
                if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
                    String keyword = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                    predicates = cb.and(predicates,
                            cb.or(
                                    cb.like(cb.lower(root.get("contractNumber")), keyword),
                                    cb.like(cb.lower(root.get("employee").get("fullName")), keyword),
                                    cb.like(cb.lower(root.get("employee").get("employeeCode")), keyword),
                                    cb.like(cb.lower(root.get("position").get("name")), keyword)
                            ));
                }
                if (filter.getContractNumber() != null && !filter.getContractNumber().trim().isEmpty()) {
                    predicates = cb.and(predicates,
                            cb.like(cb.lower(root.get("contractNumber")),
                                    "%" + filter.getContractNumber().trim().toLowerCase() + "%"));
                }
                if (filter.getEmployeeId() != null) {
                    predicates = cb.and(predicates,
                            cb.equal(root.get("employee").get("id"), filter.getEmployeeId()));
                }
                if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
                    predicates = cb.and(predicates,
                            cb.equal(cb.upper(root.get("status")), filter.getStatus().trim().toUpperCase()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates,
                            cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
                } else {
                    predicates = cb.and(predicates,
                            cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
                }
            } else {
                predicates = cb.and(predicates,
                        cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
            }

            return predicates;
        };
    }

    private void applyPayload(Contract entity, ContractRequestDto payload) {
        Employee employee = employeeRepository.findById(payload.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(Employee.class, payload.getEmployeeId()));

        Position position = null;
        if (payload.getPositionId() != null) {
            position = positionRepository.findById(payload.getPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException(Position.class, payload.getPositionId()));
        }

        entity.setEmployee(employee);
        entity.setContractNumber(payload.getContractNumber());
        entity.setBaseSalary(payload.getBaseSalary());
        entity.setActualSalary(payload.getActualSalary());
        entity.setPosition(position);
        entity.setStartDate(payload.getStartDate());
        entity.setEndDate(payload.getEndDate());
        entity.setStatus(payload.getStatus());
        entity.setIsNet(Boolean.TRUE.equals(payload.getIsNet()));
    }
}