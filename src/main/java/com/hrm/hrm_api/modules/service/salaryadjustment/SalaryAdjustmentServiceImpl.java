package com.hrm.hrm_api.modules.service.salaryadjustment;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentCreateRequest;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentSearchRequest;
import com.hrm.hrm_api.modules.dto.salaryadjustment.SalaryAdjustmentFilter;
import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.repository.SalaryAdjustmentRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SalaryAdjustmentServiceImpl implements SalaryAdjustmentService {

    private final SalaryAdjustmentRepository repository;
    private final EntityManager entityManager;

    public SalaryAdjustmentServiceImpl(SalaryAdjustmentRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    public SalaryAdjustment findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(SalaryAdjustment.class, id));
    }

    @Override
    @Transactional
    public SalaryAdjustment create(SalaryAdjustment entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public SalaryAdjustment update(UUID id, SalaryAdjustment entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(SalaryAdjustment.class, id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public SalaryAdjustment createFromRequest(SalaryAdjustmentCreateRequest request) {
        PayrollPeriod payrollPeriod = entityManager.getReference(PayrollPeriod.class, request.getPayrollPeriodId());
        Employee employee = entityManager.getReference(Employee.class, request.getEmployeeId());

        SalaryAdjustment entity = new SalaryAdjustment();
        entity.setPayrollPeriod(payrollPeriod);
        entity.setEmployee(employee);
        entity.setAmount(request.getAmount());
        entity.setType(request.getType());
        entity.setReason(request.getReason());
        entity.setIsTaxable(request.getIsTaxable());
        entity.setIsNetOnly(request.getIsNetOnly());
        return repository.save(entity);
    }

    @Override
    @Transactional
    public SalaryAdjustment updateFromRequest(UUID id, SalaryAdjustmentCreateRequest request) {
        SalaryAdjustment entity = findById(id);

        PayrollPeriod payrollPeriod = entityManager.getReference(PayrollPeriod.class, request.getPayrollPeriodId());
        Employee employee = entityManager.getReference(Employee.class, request.getEmployeeId());

        entity.setPayrollPeriod(payrollPeriod);
        entity.setEmployee(employee);
        entity.setAmount(request.getAmount());
        entity.setType(request.getType());
        entity.setReason(request.getReason());
        entity.setIsTaxable(request.getIsTaxable());
        entity.setIsNetOnly(request.getIsNetOnly());
        return repository.save(entity);
    }

    @Override
    @Transactional
    public SalaryAdjustment upsertByPayrollPeriodAndEmployee(UUID payrollPeriodId, UUID employeeId, SalaryAdjustment payload) {
        List<SalaryAdjustment> existing = repository.findByPayrollPeriodIdAndEmployeeId(payrollPeriodId, employeeId);
        if (!existing.isEmpty()) {
            // Update the first one, assuming only one per employee per period
            SalaryAdjustment adjustment = existing.get(0);
            adjustment.setAmount(payload.getAmount());
            adjustment.setType(payload.getType());
            adjustment.setReason(payload.getReason());
            adjustment.setIsTaxable(payload.getIsTaxable());
            adjustment.setIsNetOnly(payload.getIsNetOnly());
            return repository.save(adjustment);
        } else {
            // Create new
            PayrollPeriod payrollPeriod = entityManager.getReference(PayrollPeriod.class, payrollPeriodId);
            Employee employee = entityManager.getReference(Employee.class, employeeId);
            payload.setPayrollPeriod(payrollPeriod);
            payload.setEmployee(employee);
            return repository.save(payload);
        }
    }

    @Override
    public Page<SalaryAdjustment> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<SalaryAdjustment> findAll(SalaryAdjustmentFilter filter, Pageable pageable) {
        return repository.findAll(buildSpec(filter), pageable);
    }

    private Specification<SalaryAdjustment> buildSpec(SalaryAdjustmentFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction(); // Start with true

            if (filter != null) {
                if (filter.getPayrollPeriodId() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("payrollPeriod").get("id"), filter.getPayrollPeriodId()));
                }
                if (filter.getEmployeeId() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("employee").get("id"), filter.getEmployeeId()));
                }
                if (filter.getAmount() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("amount"), filter.getAmount()));
                }
                if (filter.getType() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("type"), filter.getType()));
                }
                if (filter.getReason() != null) {
                    predicates = cb.and(predicates, cb.like(root.get("reason"), "%" + filter.getReason() + "%"));
                }
                if (filter.getIsTaxable() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isTaxable"), filter.getIsTaxable()));
                }
                if (filter.getIsNetOnly() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isNetOnly"), filter.getIsNetOnly()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
                } else {
                    // Default to not deleted
                    predicates = cb.and(predicates, cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
                }
            } else {
                // Default to not deleted
                predicates = cb.and(predicates, cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
            }

            return predicates;
        };
    }

    @Override
    public PaginationResponse<SalaryAdjustment> search(SalaryAdjustmentSearchRequest request, Pageable pageable) {
        var filter = request != null ? request.getWhere() : null;

        Page<SalaryAdjustment> salaryAdjustments = repository.findAll(buildSpec(filter), pageable);

        return PaginationResponse.of(
                salaryAdjustments.getContent(),
                salaryAdjustments.getTotalElements(),
                salaryAdjustments.getNumber(),
                salaryAdjustments.getSize()
        );
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        SalaryAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryAdjustment not found"));
        adjustment.setIsDeleted(true);
        repository.save(adjustment);
    }

    @Override
    @Transactional
    public void activate(UUID id) {
        SalaryAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryAdjustment not found"));
        adjustment.setIsDeleted(false);
        repository.save(adjustment);
    }

    @Override
    @Transactional
    public List<SalaryAdjustment> batchUpsertByPayrollPeriod(UUID payrollPeriodId, List<SalaryAdjustment> adjustments) {
        PayrollPeriod payrollPeriod = entityManager.getReference(PayrollPeriod.class, payrollPeriodId);

        return adjustments.stream().map(adjustment -> {
            UUID employeeId = adjustment.getEmployee().getId();
            List<SalaryAdjustment> existing = repository.findByPayrollPeriodIdAndEmployeeId(payrollPeriodId, employeeId);
            if (!existing.isEmpty()) {
                // Update existing
                SalaryAdjustment existingAdjustment = existing.get(0);
                existingAdjustment.setAmount(adjustment.getAmount());
                existingAdjustment.setType(adjustment.getType());
                existingAdjustment.setReason(adjustment.getReason());
                existingAdjustment.setIsTaxable(adjustment.getIsTaxable());
                existingAdjustment.setIsNetOnly(adjustment.getIsNetOnly());
                return repository.save(existingAdjustment);
            } else {
                // Create new
                Employee employee = entityManager.getReference(Employee.class, employeeId);
                adjustment.setPayrollPeriod(payrollPeriod);
                adjustment.setEmployee(employee);
                return repository.save(adjustment);
            }
        }).collect(Collectors.toList());
    }
}