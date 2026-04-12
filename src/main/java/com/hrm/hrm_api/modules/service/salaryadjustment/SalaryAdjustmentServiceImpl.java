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
import com.hrm.hrm_api.modules.entity.Payslip;
import com.hrm.hrm_api.modules.repository.PayslipRepository;
import com.hrm.hrm_api.modules.repository.SalaryAdjustmentRepository;
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
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
    private final PayslipRepository payslipRepository;
    private final EntityManager entityManager;
    private final ActionLogUtil actionLogUtil;
    private final SecurityUtil securityUtil;

    public SalaryAdjustmentServiceImpl(SalaryAdjustmentRepository repository, PayslipRepository payslipRepository, EntityManager entityManager,
            ActionLogUtil actionLogUtil, SecurityUtil securityUtil) {
        this.repository = repository;
        this.payslipRepository = payslipRepository;
        this.entityManager = entityManager;
        this.actionLogUtil = actionLogUtil;
        this.securityUtil = securityUtil;
    }

    /**
     * Tìm điều chỉnh lương theo ID
     */
    @Override
    public SalaryAdjustment findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(SalaryAdjustment.class, id));
    }

    /**
     * Tạo mới điều chỉnh lương
     * Văn bản có: kỳ lương và nhân viên hợp lệ
     */
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
        entity.setIsTaxable(request.getIsTaxable());
        entity.setIsNetOnly(request.getIsNetOnly());
        entity.setReason(request.getReason());

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

    /**
     * Cập nhật điều chỉnh lương
     */
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
        entity.setIsTaxable(request.getIsTaxable());
        entity.setIsNetOnly(request.getIsNetOnly());
        entity.setReason(request.getReason());
        SalaryAdjustment updated = repository.save(entity);

        actionLogUtil.logUpdate(
                updated.getId(),
                "SALARY_ADJUSTMENT",
                "Cập nhật điều chỉnh lương",
                securityUtil.getCurrentUsername());

        return updated;
    }

    /**
     * Lấy tất cả điều chỉnh lương với phân trang
     * Mặc định chỉ lấy các điều chỉnh chưa bị xoá (isDeleted = false)
     */
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

    /**
     * Ngừng hoạt động điều chỉnh lương (soft delete)
     * Đảnh dấu isDeleted = true
     */
    @Override
    @Transactional
    public void deactivate(UUID id) {
        SalaryAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryAdjustment not found"));
        adjustment.setIsDeleted(true);
        repository.save(adjustment);

        actionLogUtil.logDeactivate(
                id,
                "SALARY_ADJUSTMENT",
                "Ngừng hoạt động điều chỉnh lương",
                securityUtil.getCurrentUsername());
    }

    /**
     * Kích hoạt lại điều chỉnh lương (khôi phục từ soft delete)
     * Đảnh dấu isDeleted = false
     */
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

        // Đánh dấu duyệt
        adjustment.setStatus(SalaryAdjustment.Status.APPROVED);

        repository.save(adjustment);

        actionLogUtil.logUpdate(
                id,
                "SALARY_ADJUSTMENT",
                "Duyệt điều chỉnh lương nhân viên: " + adjustment.getEmployee().getEmployeeCode(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Hủy duyệt điều chỉnh lương
     */
    @Override
    @Transactional
    public void unApprove(UUID id) {
        SalaryAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SalaryAdjustment.class, id));
        Payslip payslip = payslipRepository.findByPayrollPeriodAndEmployeeAndIsDeleted(adjustment.getPayrollPeriod().getId(), adjustment.getEmployee().getId(), false);
        if (payslip != null) {
            throw new IllegalStateException(
                    "Không thể hủy duyệt khi đã có phiếu lương liên quan");
        }
        // Đánh dấu hủy duyệt
        adjustment.setStatus(SalaryAdjustment.Status.PENDING);

        repository.save(adjustment);

        actionLogUtil.logCancel(
                id,
                "SALARY_ADJUSTMENT",
                "Hủy duyệt điều chỉnh lương nhân viên: " + adjustment.getEmployee().getEmployeeCode(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Duyệt nhiều điều chỉnh lương theo danh sách
     */
    @Override
    public String bulkApprove(List<UUID> ids) {
        int successCount = 0;
        int failureCount = 0;
        StringBuilder failures = new StringBuilder();

        for (UUID id : ids) {
            try {
                approve(id);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                failures.append("ID ").append(id).append(": ").append(e.getMessage()).append("; ");
            }
        }

        String message = "Duyệt thành công " + successCount + " bản ghi";
        if (failureCount > 0) {
            message += ", thất bại " + failureCount + " bản ghi: " + failures.toString().trim();
        }
        return message;
    }

    /**
     * Hủy duyệt nhiều điều chỉnh lương theo danh sách
     */
    @Override
    public String bulkUnApprove(List<UUID> ids) {
        int successCount = 0;
        int failureCount = 0;
        StringBuilder failures = new StringBuilder();

        for (UUID id : ids) {
            try {
                unApprove(id);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                failures.append("ID ").append(id).append(": ").append(e.getMessage()).append("; ");
            }
        }

        String message = "Hủy duyệt thành công " + successCount + " bản ghi";
        if (failureCount > 0) {
            message += ", thất bại " + failureCount + " bản ghi: " + failures.toString().trim();
        }
        return message;
    }

    /**
     * Lấy danh sách ID theo filter (không phân trang)
     */
    @Override
    public List<UUID> getIdsByFilter(SalaryAdjustmentSearchRequest request) {
        var filter = request != null ? request.getWhere() : null;
        Specification<SalaryAdjustment> spec = buildSpec(filter);
        List<SalaryAdjustment> adjustments = repository.findAll(spec);
        return adjustments.stream().map(SalaryAdjustment::getId).collect(Collectors.toList());
    }
}