package com.hrm.hrm_api.modules.service.attendancesummary;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.attendancesummary.AttendanceSummaryFilter;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummaryCreateRequest;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummarySearchRequest;
import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.repository.AttendanceSummaryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AttendanceSummaryServiceImpl implements AttendanceSummaryService {

    private final AttendanceSummaryRepository repository;

    public AttendanceSummaryServiceImpl(AttendanceSummaryRepository repository) {
        this.repository = repository;
    }

    @Override
    public AttendanceSummary findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(AttendanceSummary.class, id));
    }

    @Override
    @Transactional
    public AttendanceSummary create(AttendanceSummary entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public AttendanceSummary update(UUID id, AttendanceSummary entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(AttendanceSummary.class, id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public AttendanceSummary createFromRequest(AttendanceSummaryCreateRequest request) {
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
        entity.setIsApproved(request.getIsApproved());
        return repository.save(entity);
    }

    @Override
    @Transactional
    public AttendanceSummary updateFromRequest(UUID id, AttendanceSummaryCreateRequest request) {
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
        entity.setIsApproved(request.getIsApproved());
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
                if (filter.getIsApproved() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isApproved"), filter.getIsApproved()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
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
     * Duyệt công - đóng băng công cho kỳ (Bước 2)
     */
    @Override
    @Transactional
    public void approveAttendance(UUID attendanceSummaryId) {
        AttendanceSummary summary = repository.findById(attendanceSummaryId)
                .orElseThrow(() -> new RuntimeException("AttendanceSummary not found"));
        
        summary.setIsApproved(true);
        repository.save(summary);
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