package com.hrm.hrm_api.modules.service.attendancesummary;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.common.security.SecurityUtil;
import com.hrm.hrm_api.modules.dto.attendancesummary.AttendanceSummaryFilter;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummaryCreateRequest;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummarySearchRequest;
import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.repository.AttendanceSummaryRepository;
import com.hrm.hrm_api.modules.util.ActionLogUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AttendanceSummaryServiceImpl implements AttendanceSummaryService {

    private final AttendanceSummaryRepository repository;
    private final ActionLogUtil actionLogUtil;
    private final SecurityUtil securityUtil;

    public AttendanceSummaryServiceImpl(AttendanceSummaryRepository repository, ActionLogUtil actionLogUtil, SecurityUtil securityUtil) {
        this.repository = repository;
        this.actionLogUtil = actionLogUtil;
        this.securityUtil = securityUtil;
    }

    @Override
    public AttendanceSummary findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(AttendanceSummary.class, id));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(AttendanceSummary.class, id);
        }
        actionLogUtil.logDelete(
                id,
                "ATTENDANCE_SUMMARY",
                "Xóa tổng hợp chấm công: " + id,
                securityUtil.getCurrentUsername()
        );
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public AttendanceSummary create(AttendanceSummaryCreateRequest request) {
        PayrollPeriod payrollPeriod = new PayrollPeriod();
        payrollPeriod.setId(request.getPayrollPeriodId());

        Employee employee = new Employee();
        employee.setId(request.getEmployeeId());

        AttendanceSummary entity = new AttendanceSummary();
        entity.setPayrollPeriod(payrollPeriod);
        entity.setEmployee(employee);
        entity.setActualWorkingDays(request.getActualWorkingDays());
        entity.setPaidLeaveDays(request.getPaidLeaveDays());
        entity.setUnpaidLeaveDays(request.getUnpaidLeaveDays());
        entity.setOvertimeHours(request.getOvertimeHours());
        return repository.save(entity);
    }

    @Override
    @Transactional
    public AttendanceSummary update(UUID id, AttendanceSummaryCreateRequest request) {
        AttendanceSummary entity = findById(id);

        PayrollPeriod payrollPeriod = new PayrollPeriod();
        payrollPeriod.setId(request.getPayrollPeriodId());

        Employee employee = new Employee();
        employee.setId(request.getEmployeeId());

        entity.setPayrollPeriod(payrollPeriod);
        entity.setEmployee(employee);
        entity.setActualWorkingDays(request.getActualWorkingDays());
        entity.setPaidLeaveDays(request.getPaidLeaveDays());
        entity.setUnpaidLeaveDays(request.getUnpaidLeaveDays());
        entity.setOvertimeHours(request.getOvertimeHours());
        return repository.save(entity);
    }

    @Override
    public Page<AttendanceSummary> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<AttendanceSummary> findAll(AttendanceSummaryFilter filter, Pageable pageable) {
        return repository.findAll(buildSpec(filter), pageable);
    }

    private Specification<AttendanceSummary> buildSpec(AttendanceSummaryFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter != null) {
                if (filter.getPayrollPeriodId() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("payrollPeriod").get("id"), filter.getPayrollPeriodId()));
                }
                if (filter.getEmployeeId() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("employee").get("id"), filter.getEmployeeId()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
                }
                if (filter.getStatus() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("status"), filter.getStatus()));
                }
            }

            return predicates;
        };
    }

    @Override
    public PaginationResponse<AttendanceSummary> search(AttendanceSummarySearchRequest request, Pageable pageable) {
        var filter = request != null ? request.getWhere() : null;

        Page<AttendanceSummary> page = repository.findAll(buildSpec(filter), pageable);

        return PaginationResponse.of(
            page.getContent(),
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
        );
    }

    /**
     * Duyệt điều chỉnh lương
     */
    @Override
    @Transactional
    public void approve(UUID id) {
        AttendanceSummary attendanceSummary = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AttendanceSummary.class, id));

        // Kiểm tra đã lock chưa
        if (PayrollPeriod.Status.LOCKED.equals(attendanceSummary.getPayrollPeriod().getStatus())) {
            throw new IllegalStateException("Kỳ lương đã bị khóa, không thể duyệt");
        }

        // Đánh dấu duyệt
        attendanceSummary.setStatus(AttendanceSummary.Status.APPROVED);

        repository.save(attendanceSummary);


        actionLogUtil.logUpdate(
                id,
                "ATTENDANCE_SUMMARY",
                "Duyệt công: " + attendanceSummary.getPayrollPeriod().getName(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Từ chối duyệt điều chỉnh lương
     */
    @Override
    @Transactional
    public void reject(UUID id, String reason) {
        AttendanceSummary attendanceSummary = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AttendanceSummary.class, id));

        // Không cho reject khi đã lock
        if (AttendanceSummary.Status.LOCKED.equals(attendanceSummary.getStatus())) {
            throw new IllegalStateException("Kỳ lương đã bị khóa, không thể từ chối");
        }

        attendanceSummary.setStatus(AttendanceSummary.Status.REJECTED);

        repository.save(attendanceSummary);

        actionLogUtil.logUpdate(
                id,
                "ATTENDANCE_SUMMARY",
                "Từ chối duyệt công: " + attendanceSummary.getId() + " - Lý do: " + reason,
                securityUtil.getCurrentUsername());
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        AttendanceSummary entity = findById(id);
        entity.setIsDeleted(true);
        repository.save(entity);
    }

    @Override
    @Transactional
    public void activate(UUID id) {
        AttendanceSummary entity = findById(id);
        entity.setIsDeleted(false);
        repository.save(entity);
    }
}