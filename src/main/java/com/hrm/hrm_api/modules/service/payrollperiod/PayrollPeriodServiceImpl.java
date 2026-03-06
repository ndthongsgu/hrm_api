package com.hrm.hrm_api.modules.service.payrollperiod;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.payrollperiod.PayrollPeriodFilter;
import com.hrm.hrm_api.modules.dto.request.PayrollPeriodSearchRequest;
import com.hrm.hrm_api.modules.entity.*;
import com.hrm.hrm_api.modules.repository.*;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PayrollPeriodServiceImpl extends AbstractCrudService<PayrollPeriod> implements PayrollPeriodService {

    private final PayrollPeriodRepository repository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final DailyAttendanceRepository dailyAttendanceRepository;
    private final ContractRepository contractRepository;
    private final ContractAllowanceRepository contractAllowanceRepository;
    private final SalaryAdjustmentRepository salaryAdjustmentRepository;
    private final PayslipRepository payslipRepository;
    private final DependentRepository dependentRepository;
    private final InsuranceConfigRepository insuranceConfigRepository;
    private final TaxBracketRepository taxBracketRepository;
    private final ActionLogUtil actionLogUtil;

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
            ActionLogUtil actionLogUtil) {
        super(repository, PayrollPeriod.class);
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.attendanceSummaryRepository = attendanceSummaryRepository;
        this.dailyAttendanceRepository = dailyAttendanceRepository;
        this.contractRepository = contractRepository;
        this.contractAllowanceRepository = contractAllowanceRepository;
        this.salaryAdjustmentRepository = salaryAdjustmentRepository;
        this.payslipRepository = payslipRepository;
        this.dependentRepository = dependentRepository;
        this.insuranceConfigRepository = insuranceConfigRepository;
        this.taxBracketRepository = taxBracketRepository;
        this.actionLogUtil = actionLogUtil;
    }

    @Override
    public Page<PayrollPeriod> findAll(Pageable pageable) {
        return findAll(null, pageable);
    }

    @Override
    public Page<PayrollPeriod> findAll(PayrollPeriodFilter filter, Pageable pageable) {
        return repository.findAll(buildSpec(filter), pageable);
    }

    private Specification<PayrollPeriod> buildSpec(PayrollPeriodFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction(); // Start with true

            if (filter != null) {
                if (filter.getName() != null && !filter.getName().isEmpty()) {
                    predicates = cb.and(predicates, cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
                }
                if (filter.getIsLocked() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isLocked"), filter.getIsLocked()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
                }
                if (filter.getStartDate() != null) {
                    predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDate()));
                }
                if (filter.getEndDate() != null) {
                    predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("endDate"), filter.getEndDate()));
                }
                // If isDeleted is null, no filter on isDeleted (show all)
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
            payrollPeriods.getSize()
        );
    }

    /**
     * Bước 1: Tạo kỳ lương và tự động tổng hợp công
     */
    @Override
    @Transactional
    public PayrollPeriod create(PayrollPeriod payrollPeriod) {
        // Lưu kỳ lương
        PayrollPeriod savedPeriod = repository.save(payrollPeriod);
        
        // Tự động tổng hợp công cho tất cả nhân viên
        summarizeAttendance(savedPeriod.getId());
        
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
            if (attendanceSummaryRepository.findByPayrollPeriodAndEmployee(period.getId(), employee.getId()).isEmpty()) {
                // Tính tổng công từ DailyAttendance
                float actualWorkingDays = 0;
                float paidLeaveDays = 0;
                float unpaidLeaveDays = 0;
                float overtimeHours = 0;
                
                // TODO: Implement calculation logic từ daily_attendances trong kỳ
                // Cần custom query trong DailyAttendanceRepository
                
                // Tạo AttendanceSummary
                AttendanceSummary summary = new AttendanceSummary();
                summary.setPayrollPeriod(period);
                summary.setEmployee(employee);
                summary.setActualWorkingDays(actualWorkingDays);
                summary.setPaidLeaveDays(paidLeaveDays);
                summary.setUnpaidLeaveDays(unpaidLeaveDays);
                summary.setOvertimeHours(overtimeHours);
                
                attendanceSummaryRepository.save(summary);
            }
        }
    }

    /**
     * Bước 3: Tính lương cho kỳ lương
     */
    @Transactional
    public void calculatePayslips(UUID payrollPeriodId) {
        PayrollPeriod period = repository.findById(payrollPeriodId)
                .orElseThrow(() -> new RuntimeException("PayrollPeriod not found"));
        
        List<Employee> employees = employeeRepository.findAll();
        
        for (Employee employee : employees) {
            // Lấy công đã duyệt
            AttendanceSummary attendance = attendanceSummaryRepository
                    .findByPayrollPeriodAndEmployee(period.getId(), employee.getId())
                    .orElse(null);
            
            if (attendance == null) continue;
            
            // Kiểm tra công đã được duyệt
            if (!attendance.getIsApproved()) {
                continue; // Bỏ qua nhân viên chưa được duyệt công
            }
            
            // Lấy contract hiệu lực
            Contract contract = contractRepository.findByEmployeeAndStatus(employee.getId(), "ACTIVE")
                    .orElse(null);
            
            if (contract == null) continue;
            
            // Tính lương
            Payslip payslip = calculatePayslip(employee, period, contract, attendance);
            
            // Upsert payslip (tìm xem có payslip cũ không, có thì update, không thì tạo mới)
            payslipRepository.findByPayrollPeriodAndEmployee(period.getId(), employee.getId())
                    .ifPresentOrElse(
                            existing -> {
                                payslip.setId(existing.getId());
                                payslipRepository.save(payslip);
                            },
                            () -> payslipRepository.save(payslip)
                    );
        }
    }

    /**
     * Tính chi tiết lương cho một nhân viên
     */
    private Payslip calculatePayslip(Employee employee, PayrollPeriod period,
                                     Contract contract, AttendanceSummary attendance) {
        Payslip payslip = new Payslip();
        payslip.setPayrollPeriod(period);
        payslip.setEmployee(employee);
        payslip.setContract(contract);
        
        // 1. Tính thu nhập
        BigDecimal grossSalary = calculateGrossSalary(contract, period, attendance);
        payslip.setGrossSalary(grossSalary);
        
        // Lương theo ngày công
        BigDecimal salaryByWorkdays = grossSalary
                .multiply(BigDecimal.valueOf(attendance.getActualWorkingDays()))
                .divide(BigDecimal.valueOf(period.getStandardWorkingDays()), 2, BigDecimal.ROUND_HALF_UP);
        payslip.setSalaryByWorkdays(salaryByWorkdays);
        
        // Tính phụ cấp
        BigDecimal totalAllowance = calculateTotalAllowance(contract);
        payslip.setTotalAllowance(totalAllowance);
        
        // Điều chỉnh (bonus/fine)
        BigDecimal totalBonus = calculateTotalAdjustments(employee, period);
        payslip.setTotalBonus(totalBonus);
        
        // Income trước bảo hiểm = từng mục + phụ cấp + điều chỉnh
        BigDecimal incomeBeforeInsurance = salaryByWorkdays.add(totalAllowance).add(totalBonus);
        
        // 2. Tính bảo hiểm
        calculateInsurance(payslip, incomeBeforeInsurance, contract, employee);
        
        // 3. Tính thuế
        calculateTax(payslip, salaryByWorkdays, employee);
        
        // 4. Tính net
        BigDecimal netSalary = incomeBeforeInsurance
                .subtract(payslip.getBhxhAmount() != null ? payslip.getBhxhAmount() : BigDecimal.ZERO)
                .subtract(payslip.getBhytAmount() != null ? payslip.getBhytAmount() : BigDecimal.ZERO)
                .subtract(payslip.getBhtnAmount() != null ? payslip.getBhtnAmount() : BigDecimal.ZERO)
                .subtract(payslip.getTaxAmount() != null ? payslip.getTaxAmount() : BigDecimal.ZERO);
        payslip.setNetSalary(netSalary);
        
        return payslip;
    }

    private BigDecimal calculateGrossSalary(Contract contract, PayrollPeriod period, 
                                           AttendanceSummary attendance) {
        // Nếu lương net, cần convert sang gross
        if (contract.getIsNet() != null && contract.getIsNet()) {
            // TODO: Implement net to gross conversion
            return contract.getActualSalary();
        }
        return contract.getActualSalary();
    }

    private BigDecimal calculateTotalAllowance(Contract contract) {
        List<ContractAllowance> allowances = contractAllowanceRepository.findByContractId(contract.getId());
        return allowances.stream()
                .map(ContractAllowance::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalAdjustments(Employee employee, PayrollPeriod period) {
        List<SalaryAdjustment> adjustments = salaryAdjustmentRepository
                .findByEmployeeAndPayrollPeriod(employee.getId(), period.getId());
        
        BigDecimal total = BigDecimal.ZERO;
        for (SalaryAdjustment adj : adjustments) {
            if ("FINE".equals(adj.getType())) {
                total = total.subtract(adj.getAmount());
            } else {
                total = total.add(adj.getAmount());
            }
        }
        return total;
    }

    private void calculateInsurance(Payslip payslip, BigDecimal salaryBase, Contract contract, Employee employee) {
        List<InsuranceConfig> configs = insuranceConfigRepository.findAll();
        
        for (InsuranceConfig config : configs) {
            BigDecimal rate = new BigDecimal(config.getEmployeeRate().toString());
            BigDecimal amount = salaryBase.multiply(rate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            
            if ("SOCIAL".equals(config.getType())) {
                payslip.setBhxhSalaryBase(salaryBase);
                payslip.setBhxhAmount(amount);
            } else if ("HEALTH".equals(config.getType())) {
                payslip.setBhytAmount(amount);
            } else if ("UNEMPLOYMENT".equals(config.getType())) {
                payslip.setBhtnAmount(amount);
            }
        }
    }

    private void calculateTax(Payslip payslip, BigDecimal salaryByWorkdays, Employee employee) {
        BigDecimal taxableIncome = salaryByWorkdays;
        payslip.setTaxableIncome(taxableIncome);
        
        // Khấu trừ cá nhân
        BigDecimal deductionPersonal = BigDecimal.valueOf(11000000); // Theo luật hiện hành
        payslip.setDeductionPersonal(deductionPersonal);
        
        // Khấu trừ người phụ thuộc
        List<Dependent> dependents = dependentRepository.findByEmployeeAndIsQualified(employee.getId(), true);
        BigDecimal deductionDependents = BigDecimal.valueOf(4400000).multiply(BigDecimal.valueOf(dependents.size()));
        payslip.setDeductionDependents(deductionDependents);
        
        BigDecimal assessableIncome = taxableIncome.subtract(deductionPersonal).subtract(deductionDependents);
        if (assessableIncome.compareTo(BigDecimal.ZERO) < 0) {
            assessableIncome = BigDecimal.ZERO;
        }
        payslip.setAssessableIncome(assessableIncome);
        
        // Áp dụng bảng lũy tiến khấu trừ
        BigDecimal taxAmount = calculateProgressiveTax(assessableIncome);
        payslip.setTaxAmount(taxAmount);
    }

    private BigDecimal calculateProgressiveTax(BigDecimal income) {
        List<TaxBracket> brackets = taxBracketRepository.findByIsActive(true);
        
        BigDecimal tax = BigDecimal.ZERO;
        for (TaxBracket bracket : brackets) {
            if (income.compareTo(bracket.getMinIncome()) >= 0) {
                BigDecimal taxableAmount = income.min(bracket.getMaxIncome()).subtract(bracket.getMinIncome());
                BigDecimal bracketTax = taxableAmount
                        .multiply(BigDecimal.valueOf(bracket.getTaxRate()))
                        .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP)
                        .subtract(bracket.getSubtractAmount());
                tax = tax.add(bracketTax);
            }
        }
        
        return tax.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : tax;
    }

    /**
     * Bước 4: Chốt/Lock kỳ lương
     */
    @Transactional
    public void lockPeriod(UUID payrollPeriodId) {
        PayrollPeriod period = repository.findById(payrollPeriodId)
                .orElseThrow(() -> new RuntimeException("PayrollPeriod not found"));
        
        period.setIsLocked(true);
        repository.save(period);
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
    }
}

