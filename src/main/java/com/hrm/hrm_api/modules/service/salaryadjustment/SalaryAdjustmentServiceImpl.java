package com.hrm.hrm_api.modules.service.salaryadjustment;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.common.security.SecurityUtil;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentCreateRequest;
import com.hrm.hrm_api.modules.dto.request.SalaryAdjustmentSearchRequest;
import com.hrm.hrm_api.modules.dto.salaryadjustment.SalaryAdjustmentFilter;
import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.repository.SalaryAdjustmentRepository;
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SalaryAdjustmentServiceImpl implements SalaryAdjustmentService {

    private final SalaryAdjustmentRepository repository;
    private final EntityManager entityManager;
    private final ActionLogUtil actionLogUtil;
    private final SecurityUtil securityUtil;

    public SalaryAdjustmentServiceImpl(SalaryAdjustmentRepository repository, EntityManager entityManager,
            ActionLogUtil actionLogUtil, SecurityUtil securityUtil) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.actionLogUtil = actionLogUtil;
        this.securityUtil = securityUtil;
    }

    @Override
    public SalaryAdjustment findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(SalaryAdjustment.class, id));
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
    public SalaryAdjustment create(SalaryAdjustmentCreateRequest request) {
        PayrollPeriod payrollPeriod = entityManager.find(PayrollPeriod.class, request.getPayrollPeriodId());
        if (payrollPeriod == null) {
            throw new ResourceNotFoundException(PayrollPeriod.class, request.getPayrollPeriodId());
        }

        if (request.getEmployeeId() == null) {
            throw new IllegalArgumentException("Nhân viên không được để trống");
        }

        Employee employee = entityManager.find(Employee.class, request.getEmployeeId());
        if (employee == null) {
            throw new ResourceNotFoundException(Employee.class, request.getEmployeeId());
        }

        SalaryAdjustment entity = new SalaryAdjustment();
        entity.setPayrollPeriod(payrollPeriod);
        entity.setEmployee(employee);
        entity.setAmount(request.getAmount());
        entity.setType(request.getType());
        entity.setReason(request.getReason());
        entity.setIsTaxable(request.getIsTaxable());
        entity.setIsNetOnly(request.getIsNetOnly());

        SalaryAdjustment saved = repository.save(entity);

        try {
            actionLogUtil.logCreate(
                    saved.getId(),
                    "SALARY_ADJUSTMENT",
                    "Tạo điều chỉnh lương",
                    securityUtil.getCurrentUsername());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi ghi log hành động: " + e.getMessage(), e);
        }

        return saved;
    }

    @Override
    @Transactional
    public SalaryAdjustment update(UUID id, SalaryAdjustmentCreateRequest request) {
        SalaryAdjustment entity = findById(id);

        PayrollPeriod payrollPeriod = entityManager.find(PayrollPeriod.class, request.getPayrollPeriodId());
        if (payrollPeriod == null) {
            throw new ResourceNotFoundException(PayrollPeriod.class, request.getPayrollPeriodId());
        }

        Employee employee = entityManager.find(Employee.class, request.getEmployeeId());
        if (employee == null) {
            throw new ResourceNotFoundException(Employee.class, request.getEmployeeId());
        }

        entity.setPayrollPeriod(payrollPeriod);
        entity.setEmployee(employee);
        entity.setAmount(request.getAmount());
        entity.setType(request.getType());
        entity.setReason(request.getReason());
        entity.setIsTaxable(request.getIsTaxable());
        entity.setIsNetOnly(request.getIsNetOnly());
        SalaryAdjustment updated = repository.save(entity);

        actionLogUtil.logUpdate(
                updated.getId(),
                "SALARY_ADJUSTMENT",
                "Cập nhật điều chỉnh lương",
                securityUtil.getCurrentUsername());

        return updated;
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
                    predicates = cb.and(predicates,
                            cb.equal(root.get("payrollPeriod").get("id"), filter.getPayrollPeriodId()));
                }
                if (filter.getEmployeeId() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("employee").get("id"), filter.getEmployeeId()));
                }
                if (filter.getStatus() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("status"), filter.getStatus()));
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
                    predicates = cb.and(predicates,
                            cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
                }
            } else {
                // Default to not deleted
                predicates = cb.and(predicates,
                        cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
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
                salaryAdjustments.getSize());
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        SalaryAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryAdjustment not found"));
        adjustment.setIsDeleted(true);
        repository.save(adjustment);

        actionLogUtil.logDelete(
                id,
                "SALARY_ADJUSTMENT",
                "Ngừng hoạt động điều chỉnh lương",
                securityUtil.getCurrentUsername());
    }

    @Override
    @Transactional
    public void activate(UUID id) {
        SalaryAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryAdjustment not found"));
        adjustment.setIsDeleted(false);
        repository.save(adjustment);

        actionLogUtil.logActivate(
                id,
                "SALARY_ADJUSTMENT",
                "Kích hoạt lại điều chỉnh lương",
                securityUtil.getCurrentUsername());
    }

    /**
     * Duyệt điều chỉnh lương
     */
    @Override
    @Transactional
    public void approve(UUID id) {
        SalaryAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SalaryAdjustment.class, id));

        // Kiểm tra đã lock chưa
        if (PayrollPeriod.Status.LOCKED.equals(adjustment.getPayrollPeriod().getStatus())) {
            throw new IllegalStateException("Kỳ lương đã bị khóa, không thể duyệt");
        }

        // Đánh dấu duyệt
        adjustment.setStatus(SalaryAdjustment.Status.APPROVED);

        repository.save(adjustment);


        actionLogUtil.logUpdate(
                id,
                "PAYROLL_PERIOD",
                "Duyệt kỳ lương: " + adjustment.getPayrollPeriod().getName(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Từ chối duyệt điều chỉnh lương
     */
    @Override
    @Transactional
    public void reject(UUID id, String reason) {
        SalaryAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SalaryAdjustment.class, id));

        // Không cho reject khi đã lock
        if (SalaryAdjustment.Status.LOCKED.equals(adjustment.getStatus())) {
            throw new IllegalStateException("Điều chỉnh lương đã bị khóa, không thể từ chối");
        }

        adjustment.setStatus(SalaryAdjustment.Status.REJECTED);

        repository.save(adjustment);

        actionLogUtil.logUpdate(
                id,
                "SALARY_ADJUSTMENT",
                "Từ chối duyệt điều chỉnh lương: " + adjustment.getId() + " - Lý do: " + reason,
                securityUtil.getCurrentUsername());
    }
}