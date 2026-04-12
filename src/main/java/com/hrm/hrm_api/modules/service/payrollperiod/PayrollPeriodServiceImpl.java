package com.hrm.hrm_api.modules.service.payrollperiod;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.common.security.SecurityUtil;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.payrollperiod.PayrollPeriodFilter;
import com.hrm.hrm_api.modules.dto.request.PayrollPeriodSearchRequest;
import com.hrm.hrm_api.modules.entity.*;
import com.hrm.hrm_api.modules.repository.*;
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PayrollPeriodServiceImpl implements PayrollPeriodService {

    private final PayrollPeriodRepository repository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final SalaryAdjustmentRepository salaryAdjustmentRepository;
    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final PayslipRepository payslipRepository;
    private final ActionLogUtil actionLogUtil;
    private final SecurityUtil securityUtil;

    public PayrollPeriodServiceImpl(
            PayrollPeriodRepository repository,
            EmployeeRepository employeeRepository,
            AttendanceSummaryRepository attendanceSummaryRepository,
            DailyAttendanceRepository dailyAttendanceRepository,
            ContractRepository contractRepository,
            ContractAllowanceRepository contractAllowanceRepository,
            SalaryAdjustmentRepository salaryAdjustmentRepository,
            PayslipRepository payslipRepository,
            DependentRepository dependentRepository,
            InsuranceConfigRepository insuranceConfigRepository,
            TaxBracketRepository taxBracketRepository,
            ActionLogUtil actionLogUtil,
            SecurityUtil securityUtil) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.attendanceSummaryRepository = attendanceSummaryRepository;
        this.dailyAttendanceRepository = dailyAttendanceRepository;
        this.salaryAdjustmentRepository = salaryAdjustmentRepository;
        this.payslipRepository = payslipRepository;
        this.actionLogUtil = actionLogUtil;
        this.securityUtil = securityUtil;
    }

    /**
     * Tìm kỳ lương theo ID
     */
    @Override
    public PayrollPeriod findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(PayrollPeriod.class, id));
    }

    @Override
    @Transactional
    public PayrollPeriod update(UUID id, PayrollPeriod entity) {
        // Fetch existing entity from DB to maintain version for optimistic locking
        PayrollPeriod existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PayrollPeriod.class, id));

        // Update only the fields that should be changed
        existing.setName(entity.getName());
        existing.setStartDate(entity.getStartDate());
        existing.setEndDate(entity.getEndDate());
        existing.setStandardWorkingDays(entity.getStandardWorkingDays());
        existing.setStatus(entity.getStatus());

        PayrollPeriod updated = repository.save(existing);

        actionLogUtil.logUpdate(
                id,
                "PAYROLL_PERIOD",
                "Cập nhật kỳ lương: " + updated.getName(),
                securityUtil.getCurrentUsername());

        return updated;
    }

    private Specification<PayrollPeriod> buildSpec(PayrollPeriodFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction(); // Start with true

            if (filter != null) {
                if (filter.getName() != null && !filter.getName().isEmpty()) {
                    predicates = cb.and(predicates,
                            cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
                }
                if (filter.getStatus() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("status"), filter.getStatus()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
                }
                if (filter.getStartDate() != null) {
                    predicates = cb.and(predicates,
                            cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDate()));
                }
                if (filter.getEndDate() != null) {
                    predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("endDate"), filter.getEndDate()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
                }
            }
            return predicates;
        };
    }

    @Override
    public PaginationResponse<PayrollPeriod> search(PayrollPeriodSearchRequest request, Pageable pageable) {
        var filter = request != null ? request.getWhere() : null;

        Page<PayrollPeriod> payrollPeriods = repository.findAll(buildSpec(filter), pageable);

        return PaginationResponse.of(
                payrollPeriods.getContent(),
                payrollPeriods.getTotalElements(),
                payrollPeriods.getNumber(),
                payrollPeriods.getSize());
    }

    @Override
    public List<SelectOptionDto> getSelectOptions() {
        List<PayrollPeriod> payrollPeriods = repository.findAllByIsDeletedAndStatusOrderByCreatedAtDesc(false,
                PayrollPeriod.Status.APPROVED);
        return payrollPeriods.stream()
                .map(period -> new SelectOptionDto(period.getId(), period.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Bước 1: Tạo kỳ lương và tự động tổng hợp công
     */
    @Override
    @Transactional
    public PayrollPeriod create(PayrollPeriod payrollPeriod) {
        // Lưu kỳ lương
        PayrollPeriod savedPeriod = repository.save(payrollPeriod);

        actionLogUtil.logCreate(
                savedPeriod.getId(),
                "PAYROLL_PERIOD",
                "Tạo kỳ lương: " + savedPeriod.getName(),
                securityUtil.getCurrentUsername());


        return savedPeriod;
    }

    public PayrollPeriod update(PayrollPeriod payrollPeriod) {
        // Lưu kỳ lương
        return repository.save(payrollPeriod);
    }

    /**
     * Tự động tổng hợp công từ daily_attendances
     */
    @Transactional
    public void summarizeAttendance(UUID payrollPeriodId) {
        PayrollPeriod period = repository.findById(payrollPeriodId)
                .orElseThrow(() -> new RuntimeException("PayrollPeriod not found"));

        // Lấy tất cả nhân viên
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            // Kiểm tra đã có summary cho kỳ này chưa
            if (attendanceSummaryRepository.findByPayrollPeriodAndEmployee(period.getId(), employee.getId())
                    .isEmpty()) {
                // Tính tổng công từ DailyAttendance
                Long actualWorkingDaysCount = dailyAttendanceRepository.countActualWorkingDays(
                        employee.getId(),
                        period.getStartDate(),
                        period.getEndDate());
                float actualWorkingDays = actualWorkingDaysCount != null ? actualWorkingDaysCount.floatValue() : 0f;

                Long paidLeaveDaysCount = dailyAttendanceRepository.countPaidLeaveDays(
                        employee.getId(),
                        period.getStartDate(),
                        period.getEndDate());
                float paidLeaveDays = paidLeaveDaysCount != null ? paidLeaveDaysCount.floatValue() : 0f;

                Long unpaidLeaveDaysCount = dailyAttendanceRepository.countUnpaidLeaveDays(
                        employee.getId(),
                        period.getStartDate(),
                        period.getEndDate());
                float unpaidLeaveDays = unpaidLeaveDaysCount != null ? unpaidLeaveDaysCount.floatValue() : 0f;

                Double overtimeHoursValue = dailyAttendanceRepository.sumOvertimeHours(
                        employee.getId(),
                        period.getStartDate(),
                        period.getEndDate());
                float overtimeHours = overtimeHoursValue != null ? overtimeHoursValue.floatValue() : 0f;

                // Tạo AttendanceSummary
                AttendanceSummary summary = new AttendanceSummary();
                summary.setPayrollPeriod(period);
                summary.setEmployee(employee);
                summary.setActualWorkingDays(actualWorkingDays);
                summary.setPaidLeaveDays(paidLeaveDays);
                summary.setUnpaidLeaveDays(unpaidLeaveDays);
                summary.setOvertimeHours(overtimeHours);

                attendanceSummaryRepository.save(summary);

                // Lưu log hành động
                actionLogUtil.logCreate(
                        summary.getId(),
                        "ATTENDANCE_SUMMARY",
                        "Tạo tổng hợp công cho nhân viên: " + employee.getFullName() + " - Kỳ lương: "
                                + period.getName(),
                        securityUtil.getCurrentUsername());
            }
        }
    }

    /**
     * Soft delete: Deactivate payroll period
     */
    @Transactional
    public void deactivate(UUID payrollPeriodId) {
        PayrollPeriod period = repository.findById(payrollPeriodId)
                .orElseThrow(() -> new RuntimeException("PayrollPeriod not found"));
        period.setIsDeleted(true);
        repository.save(period);

        actionLogUtil.logDeactivate(
                payrollPeriodId,
                "PAYROLL_PERIOD",
                "Ngừng hoạt động kỳ lương: " + period.getName(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Activate payroll period (recover from soft delete)
     */
    @Transactional
    public void activate(UUID payrollPeriodId) {
        PayrollPeriod period = repository.findById(payrollPeriodId)
                .orElseThrow(() -> new RuntimeException("PayrollPeriod not found"));

        period.setIsDeleted(false);
        repository.save(period);

        actionLogUtil.logActivate(
                payrollPeriodId,
                "PAYROLL_PERIOD",
                "Kích hoạt lại kỳ lương: " + period.getName(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Duyệt kỳ lương
     */
    @Override
    @Transactional
    public void approve(UUID id) {
        PayrollPeriod period = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PayrollPeriod.class, id));

        // Đánh dấu duyệt
        period.setStatus(PayrollPeriod.Status.APPROVED);

        repository.save(period);

        // Tự động tổng hợp công cho tất cả nhân viên
        summarizeAttendance(period.getId());

        actionLogUtil.logUpdate(
                id,
                "PAYROLL_PERIOD",
                "Duyệt kỳ lương: " + period.getName(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Hủy duyệt kỳ lương
     */
    @Override
    @Transactional
    public void unApprove(UUID id) {
        PayrollPeriod period = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PayrollPeriod.class, id));
        // Kiểm tra nếu đã có payslip, salaryAdjustment liên quan thì không cho hủy
        // duyệt
        List<Payslip> payslip = payslipRepository.findByPayrollPeriodAndIsDeleted(period.getId(), false);
        List<SalaryAdjustment> salaryAdjustment = salaryAdjustmentRepository.findByPayrollPeriodAndIsDeleted(period.getId(), false);
        if (!payslip.isEmpty()) {
            throw new IllegalStateException(
                    "Không thể hủy duyệt khi đã có phiếu lương liên quan");
        }
        if (!salaryAdjustment.isEmpty()) {
            throw new IllegalStateException(
                    "Không thể hủy duyệt khi đã có điều chỉnh lương liên quan");
        }

        // Đánh dấu hủy duyệt
        period.setStatus(PayrollPeriod.Status.PENDING);

        repository.save(period);

        actionLogUtil.logCancel(
                id,
                "PAYROLL_PERIOD",
                "Hủy duyệt kỳ lương: " + period.getName(),
                securityUtil.getCurrentUsername());
    }

    @Override
    public List<SelectOptionDto> getMobileSelectOptions() {
        List<PayrollPeriod> payrollPeriods = repository.findAllByIsDeletedAndStatusOrderByCreatedAtDesc(false,
                PayrollPeriod.Status.APPROVED);
        return payrollPeriods.stream()
                .map(period -> new SelectOptionDto(period.getId(), period.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Duyệt nhiều kỳ lương theo danh sách
     */
    @Override
    @Transactional
    public void bulkApprove(List<UUID> ids) {
        for (UUID id : ids) {
            approve(id);
        }
    }

    /**
     * Hủy duyệt nhiều kỳ lương theo danh sách
     */
    @Override
    @Transactional
    public void bulkUnApprove(List<UUID> ids) {
        for (UUID id : ids) {
            unApprove(id);
        }
    }
}
