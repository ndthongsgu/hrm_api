package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import com.hrm.hrm_api.modules.entity.Contract;
import com.hrm.hrm_api.modules.entity.ContractAllowance;
import com.hrm.hrm_api.modules.entity.InsuranceConfig;
import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import com.hrm.hrm_api.modules.entity.Payslip;
import com.hrm.hrm_api.modules.entity.SalaryAdjustment;
import com.hrm.hrm_api.modules.entity.SysSetting;
import com.hrm.hrm_api.modules.entity.TaxBracket;
import com.hrm.hrm_api.modules.repository.AttendanceSummaryRepository;
import com.hrm.hrm_api.modules.repository.ContractAllowanceRepository;
import com.hrm.hrm_api.modules.repository.ContractRepository;
import com.hrm.hrm_api.modules.repository.DependentRepository;
import com.hrm.hrm_api.modules.repository.InsuranceConfigRepository;
import com.hrm.hrm_api.modules.repository.PayrollPeriodRepository;
import com.hrm.hrm_api.modules.repository.PayslipRepository;
import com.hrm.hrm_api.modules.repository.SalaryAdjustmentRepository;
import com.hrm.hrm_api.modules.repository.SysSettingRepository;
import com.hrm.hrm_api.modules.repository.TaxBracketRepository;
import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PayslipServiceImpl implements PayslipService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final PayslipRepository payslipRepository;
    private final PayrollPeriodRepository payrollPeriodRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final ContractRepository contractRepository;
    private final ContractAllowanceRepository contractAllowanceRepository;
    private final SalaryAdjustmentRepository salaryAdjustmentRepository;
    private final DependentRepository dependentRepository;
    private final SysSettingRepository sysSettingRepository;
    private final TaxBracketRepository taxBracketRepository;
    private final InsuranceConfigRepository insuranceConfigRepository;

    public PayslipServiceImpl(
            PayslipRepository repository,
            PayrollPeriodRepository payrollPeriodRepository,
            AttendanceSummaryRepository attendanceSummaryRepository,
            ContractRepository contractRepository,
            ContractAllowanceRepository contractAllowanceRepository,
            SalaryAdjustmentRepository salaryAdjustmentRepository,
            DependentRepository dependentRepository,
            SysSettingRepository sysSettingRepository,
            TaxBracketRepository taxBracketRepository,
            InsuranceConfigRepository insuranceConfigRepository
    ) {
        this.payslipRepository = repository;
        this.payrollPeriodRepository = payrollPeriodRepository;
        this.attendanceSummaryRepository = attendanceSummaryRepository;
        this.contractRepository = contractRepository;
        this.contractAllowanceRepository = contractAllowanceRepository;
        this.salaryAdjustmentRepository = salaryAdjustmentRepository;
        this.dependentRepository = dependentRepository;
        this.sysSettingRepository = sysSettingRepository;
        this.taxBracketRepository = taxBracketRepository;
        this.insuranceConfigRepository = insuranceConfigRepository;
    }

    @Override
    public Page<Payslip> findAll(Pageable pageable) {
        return payslipRepository.findAll(pageable);
    }

    @Override
    public Page<Payslip> search(java.util.Map<String, Object> filters, Pageable pageable) {
        String employeeName = (String) filters.getOrDefault("employeeName", "");
        String payrollPeriodId = (String) filters.getOrDefault("payrollPeriodId", "");
        String status = (String) filters.getOrDefault("status", "");

        return payslipRepository.findAll((root, query, criteriaBuilder) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (employeeName != null && !employeeName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("employee").get("name")),
                    "%" + employeeName.toLowerCase() + "%"
                ));
            }

            if (payrollPeriodId != null && !payrollPeriodId.isEmpty()) {
                try {
                    UUID periodId = UUID.fromString(payrollPeriodId);
                    predicates.add(criteriaBuilder.equal(root.get("payrollPeriod").get("id"), periodId));
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format, ignore filter
                }
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable);
    }

    @Override
    public Payslip findById(UUID id) {
        return payslipRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Payslip.class, id));
    }

    @Override
    @Transactional
    public Payslip create(Payslip entity) {
        return payslipRepository.save(entity);
    }

    @Override
    @Transactional
    public Payslip update(UUID id, Payslip entity) {
        findById(id);
        entity.setId(id);
        return payslipRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!payslipRepository.existsById(id)) {
            throw new ResourceNotFoundException(Payslip.class, id);
        }
        payslipRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Payslip payslip = findById(id);
        payslip.setIsDeleted(true);
        payslipRepository.save(payslip);
    }

    @Override
    @Transactional
    public void activate(UUID id) {
        Payslip payslip = findById(id);
        payslip.setIsDeleted(false);
        payslipRepository.save(payslip);
    }

    @Override
    @Transactional
    public void approve(UUID id) {
        Payslip payslip = findById(id);
        payslip.setStatus(Payslip.Status.APPROVED);
        payslipRepository.save(payslip);
    }

    @Override
    @Transactional
    public void reject(UUID id, String reason) {
        Payslip payslip = findById(id);
        payslip.setStatus(Payslip.Status.REJECTED);
        if (reason != null) {
            payslip.setReason(reason);
        }
        payslipRepository.save(payslip);
    }

    @Override
    @Transactional
    public List<Payslip> generateForPayrollPeriod(UUID payrollPeriodId) {
        PayrollPeriod period = payrollPeriodRepository.findById(payrollPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("Payroll period not found: " + payrollPeriodId));

        List<AttendanceSummary> summaries = attendanceSummaryRepository.findByPayrollPeriodId(payrollPeriodId);
        List<Payslip> result = new ArrayList<>();

        LocalDate calculationDate = period.getEndDate() != null ? period.getEndDate() : LocalDate.now();
        BigDecimal personalDeduction = getSettingValue("pit_deduction_personal", calculationDate);
        BigDecimal dependentDeductionUnit = getSettingValue("pit_deduction_dependent", calculationDate);

        for (AttendanceSummary summary : summaries) {
            if (summary.getEmployee() == null) {
                continue;
            }

            UUID employeeId = summary.getEmployee().getId();
            Optional<Contract> contractOpt = findEffectiveContract(employeeId, calculationDate);
            if (contractOpt.isEmpty()) {
                continue;
            }
            Contract contract = contractOpt.get();

            BigDecimal grossSalary = nvl(contract.getActualSalary());
            BigDecimal standardDays = toDecimal(period.getStandardWorkingDays() == null ? 26F : period.getStandardWorkingDays());
            BigDecimal actualDays = toDecimal(summary.getActualWorkingDays() == null ? standardDays.floatValue() : summary.getActualWorkingDays());

            BigDecimal salaryByWorkdays = divide(grossSalary.multiply(actualDays), standardDays);

            List<ContractAllowance> allowances = contractAllowanceRepository.findByContractId(contract.getId());
            BigDecimal totalAllowance = allowances.stream()
                    .map(a -> nvl(a.getAmount()))
                    .reduce(ZERO, BigDecimal::add);
            BigDecimal taxableAllowance = allowances.stream()
                    .filter(a -> a.getAllowanceType() != null && Boolean.TRUE.equals(a.getAllowanceType().getIsTaxable()))
                    .map(a -> nvl(a.getAmount()))
                    .reduce(ZERO, BigDecimal::add);
            BigDecimal nonTaxableAllowance = totalAllowance.subtract(taxableAllowance);

            List<SalaryAdjustment> adjustments = salaryAdjustmentRepository.findByPayrollPeriodIdAndEmployeeId(payrollPeriodId, employeeId);
            BigDecimal bonus = sumByType(adjustments, "BONUS");
            BigDecimal fine = sumByType(adjustments, "FINE");
            BigDecimal advance = sumByType(adjustments, "ADVANCE");
            BigDecimal bhxhBenefit = sumByType(adjustments, "BHXH_BENEFIT");

            BigDecimal taxableAdjustment = adjustments.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsTaxable()))
                    .map(a -> nvl(a.getAmount()))
                    .reduce(ZERO, BigDecimal::add);
            BigDecimal nonTaxableAdjustment = adjustments.stream()
                    .filter(a -> Boolean.FALSE.equals(a.getIsTaxable()))
                    .map(a -> nvl(a.getAmount()))
                    .reduce(ZERO, BigDecimal::add)
                    .add(bhxhBenefit);

            BigDecimal totalBonus = bonus.subtract(fine);
            BigDecimal incomeBeforeInsurance = salaryByWorkdays.add(totalAllowance).add(totalBonus);

            BigDecimal baseSalary = nvl(contract.getBaseSalary());
            BigDecimal bhxhBase = calculateInsuranceBase(baseSalary, "SOCIAL", calculationDate);
            BigDecimal socialRate = getInsuranceEmployeeRate("SOCIAL", calculationDate);
            BigDecimal healthRate = getInsuranceEmployeeRate("HEALTH", calculationDate);
            BigDecimal unemploymentRate = getInsuranceEmployeeRate("UNEMPLOYMENT", calculationDate);

            BigDecimal bhxhAmount = percent(bhxhBase, socialRate);
            BigDecimal bhytAmount = percent(bhxhBase, healthRate);
            BigDecimal bhtnAmount = percent(bhxhBase, unemploymentRate);
            BigDecimal totalInsurance = bhxhAmount.add(bhytAmount).add(bhtnAmount);

            long dependentCount = dependentRepository.countByEmployeeIdAndIsQualifiedTrue(employeeId);
            BigDecimal deductionDependents = dependentDeductionUnit.multiply(BigDecimal.valueOf(dependentCount));

            BigDecimal taxableIncome = salaryByWorkdays
                    .add(taxableAllowance)
                    .add(taxableAdjustment)
                    .subtract(totalInsurance)
                    .max(ZERO);

            BigDecimal assessableIncome = taxableIncome
                    .subtract(personalDeduction)
                    .subtract(deductionDependents)
                    .max(ZERO);

            BigDecimal taxAmount = calculatePit(assessableIncome, calculationDate);

            BigDecimal netSalary = incomeBeforeInsurance
                    .add(nonTaxableAllowance)
                    .add(nonTaxableAdjustment)
                    .subtract(totalInsurance)
                    .subtract(taxAmount)
                    .subtract(advance)
                    .setScale(2, RoundingMode.HALF_UP);

            Payslip payslip = payslipRepository.findByPayrollPeriodIdAndEmployeeId(payrollPeriodId, employeeId)
                    .orElseGet(Payslip::new);
            payslip.setPayrollPeriod(period);
            payslip.setEmployee(summary.getEmployee());
            payslip.setContract(contract);
            payslip.setGrossSalary(grossSalary);
            payslip.setSalaryByWorkdays(salaryByWorkdays);
            payslip.setTotalAllowance(totalAllowance);
            payslip.setTotalBonus(totalBonus);
            payslip.setBhxhSalaryBase(bhxhBase);
            payslip.setBhxhAmount(bhxhAmount);
            payslip.setBhytAmount(bhytAmount);
            payslip.setBhtnAmount(bhtnAmount);
            payslip.setSocialInsuranceBenefitAmount(bhxhBenefit);
            payslip.setTaxableIncome(taxableIncome);
            payslip.setDeductionPersonal(personalDeduction);
            payslip.setDeductionDependents(deductionDependents);
            payslip.setAssessableIncome(assessableIncome);
            payslip.setTaxAmount(taxAmount);
            payslip.setNetSalary(netSalary);

            result.add(payslipRepository.save(payslip));
        }

        return result;
    }

    @Override
    @Transactional
    public Payslip generateForEmployee(UUID payrollPeriodId, UUID employeeId) {
        // Kiểm tra payslip đã tồn tại chưa - MUST BE FIRST
        Optional<Payslip> existingPayslip = payslipRepository.findByPayrollPeriodIdAndEmployeeId(payrollPeriodId, employeeId);
        if (existingPayslip.isPresent()) {
            throw new IllegalStateException("Payslip đã tồn tại cho nhân viên này trong kỳ lương này. Vui lòng cập nhật thay vì tạo mới.");
        }

        PayrollPeriod period = payrollPeriodRepository.findById(payrollPeriodId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ lương: " + payrollPeriodId));

        List<AttendanceSummary> summaries = attendanceSummaryRepository.findByPayrollPeriodId(payrollPeriodId);
        AttendanceSummary summary = summaries.stream()
                .filter(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tổng hợp công cho nhân viên: " + employeeId));

        if (summary.getEmployee() == null) {
            throw new IllegalArgumentException("Dữ liệu nhân viên không hợp lệ trong tổng hợp công");
        }

        LocalDate calculationDate = period.getEndDate() != null ? period.getEndDate() : LocalDate.now();
        Optional<Contract> contractOpt = findEffectiveContract(employeeId, calculationDate);

        if (contractOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy hợp đồng lao động có hiệu lực cho nhân viên: " + employeeId);
        }
        Contract contract = contractOpt.get();

        BigDecimal personalDeduction = getSettingValue("pit_deduction_personal", calculationDate);
        BigDecimal dependentDeductionUnit = getSettingValue("pit_deduction_dependent", calculationDate);

        BigDecimal grossSalary = nvl(contract.getActualSalary());
        BigDecimal standardDays = toDecimal(period.getStandardWorkingDays() == null ? 26F : period.getStandardWorkingDays());
        BigDecimal actualDays = toDecimal(summary.getActualWorkingDays() == null ? standardDays.floatValue() : summary.getActualWorkingDays());

        BigDecimal salaryByWorkdays = divide(grossSalary.multiply(actualDays), standardDays);

        List<ContractAllowance> allowances = contractAllowanceRepository.findByContractId(contract.getId());
        BigDecimal totalAllowance = allowances.stream()
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add);
        BigDecimal taxableAllowance = allowances.stream()
                .filter(a -> a.getAllowanceType() != null && Boolean.TRUE.equals(a.getAllowanceType().getIsTaxable()))
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add);
        BigDecimal nonTaxableAllowance = totalAllowance.subtract(taxableAllowance);

        List<SalaryAdjustment> adjustments = salaryAdjustmentRepository.findByPayrollPeriodIdAndEmployeeId(payrollPeriodId, employeeId);
        BigDecimal bonus = sumByType(adjustments, SalaryAdjustment.Type.BONUS.name());
        BigDecimal fine = sumByType(adjustments, SalaryAdjustment.Type.FINE.name());
        BigDecimal advance = sumByType(adjustments, SalaryAdjustment.Type.ADVANCE.name());
        BigDecimal bhxhBenefit = sumByType(adjustments, SalaryAdjustment.Type.BHXH_BENEFIT.name());

        BigDecimal taxableAdjustment = adjustments.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsTaxable()))
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add);
        BigDecimal nonTaxableAdjustment = adjustments.stream()
                .filter(a -> Boolean.FALSE.equals(a.getIsTaxable()))
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add)
                .add(bhxhBenefit);

        BigDecimal totalBonus = bonus.subtract(fine);
        BigDecimal incomeBeforeInsurance = salaryByWorkdays.add(totalAllowance).add(totalBonus);

        BigDecimal baseSalary = nvl(contract.getBaseSalary());
        BigDecimal bhxhBase = calculateInsuranceBase(baseSalary, "SOCIAL", calculationDate);
        BigDecimal socialRate = getInsuranceEmployeeRate("SOCIAL", calculationDate);
        BigDecimal healthRate = getInsuranceEmployeeRate("HEALTH", calculationDate);
        BigDecimal unemploymentRate = getInsuranceEmployeeRate("UNEMPLOYMENT", calculationDate);

        BigDecimal bhxhAmount = percent(bhxhBase, socialRate);
        BigDecimal bhytAmount = percent(bhxhBase, healthRate);
        BigDecimal bhtnAmount = percent(bhxhBase, unemploymentRate);
        BigDecimal totalInsurance = bhxhAmount.add(bhytAmount).add(bhtnAmount);

        long dependentCount = dependentRepository.countByEmployeeIdAndIsQualifiedTrue(employeeId);
        BigDecimal deductionDependents = dependentDeductionUnit.multiply(BigDecimal.valueOf(dependentCount));

        BigDecimal taxableIncome = salaryByWorkdays
                .add(taxableAllowance)
                .add(taxableAdjustment)
                .subtract(totalInsurance)
                .max(ZERO);

        BigDecimal assessableIncome = taxableIncome
                .subtract(personalDeduction)
                .subtract(deductionDependents)
                .max(ZERO);

        BigDecimal taxAmount = calculatePit(assessableIncome, calculationDate);

        BigDecimal netSalary = incomeBeforeInsurance
                .add(nonTaxableAllowance)
                .add(nonTaxableAdjustment)
                .subtract(totalInsurance)
                .subtract(taxAmount)
                .subtract(advance)
                .setScale(2, RoundingMode.HALF_UP);

        // Tạo payslip mới (KHÔNG dùng orElseGet)
        Payslip payslip = new Payslip();
        payslip.setPayrollPeriod(period);
        payslip.setEmployee(summary.getEmployee());
        payslip.setContract(contract);
        payslip.setGrossSalary(grossSalary);
        payslip.setSalaryByWorkdays(salaryByWorkdays);
        payslip.setTotalAllowance(totalAllowance);
        payslip.setTotalBonus(totalBonus);
        payslip.setBhxhSalaryBase(bhxhBase);
        payslip.setBhxhAmount(bhxhAmount);
        payslip.setBhytAmount(bhytAmount);
        payslip.setBhtnAmount(bhtnAmount);
        payslip.setSocialInsuranceBenefitAmount(bhxhBenefit);
        payslip.setTaxableIncome(taxableIncome);
        payslip.setDeductionPersonal(personalDeduction);
        payslip.setDeductionDependents(deductionDependents);
        payslip.setAssessableIncome(assessableIncome);
        payslip.setTaxAmount(taxAmount);
        payslip.setNetSalary(netSalary);

        return payslipRepository.save(payslip);
    }

    private Optional<Contract> findEffectiveContract(UUID employeeId, LocalDate date) {
        return contractRepository.findByEmployeeIdAndStatusAndStartDateLessThanEqualOrderByStartDateDesc(employeeId, "ACTIVE", date)
                .stream()
                .filter(c -> c.getEndDate() == null || !c.getEndDate().isBefore(date))
                .findFirst();
    }

    private BigDecimal getSettingValue(String key, LocalDate effectiveDate) {
        return sysSettingRepository.findTopByKeyAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(key, effectiveDate)
                .map(SysSetting::getValue)
                .map(this::nvl)
                .orElse(ZERO);
    }

    private BigDecimal getInsuranceEmployeeRate(String type, LocalDate effectiveDate) {
        BigDecimal rate = insuranceConfigRepository.findTopByTypeAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(type, effectiveDate)
                .map(InsuranceConfig::getEmployeeRate)
                .map(this::nvl)
                .orElse(ZERO);
        return normalizeRate(rate);
    }

    private BigDecimal calculateInsuranceBase(BigDecimal baseSalary, String type, LocalDate date) {
        Optional<InsuranceConfig> cfgOpt = insuranceConfigRepository
                .findTopByTypeAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(type, date);
        if (cfgOpt.isEmpty()) {
            cfgOpt = insuranceConfigRepository.findTopByTypeOrderByCreatedAtDesc(type);
        }
        if (cfgOpt.isEmpty()) {
            return nvl(baseSalary);
        }

        InsuranceConfig cfg = cfgOpt.get();
        BigDecimal capBase;
        if ("REGION_MIN".equalsIgnoreCase(cfg.getCapBaseType())) {
            capBase = getSettingValue("region_min_salary_v1", date);
        } else {
            capBase = getSettingValue("reference_salary", date);
        }

        BigDecimal cap = capBase.multiply(BigDecimal.valueOf(cfg.getCapMultiplier() == null ? 1 : cfg.getCapMultiplier()));
        return nvl(baseSalary).min(cap).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePit(BigDecimal assessableIncome, LocalDate effectiveDate) {
        List<TaxBracket> brackets = taxBracketRepository.findByIsActiveTrueAndEffectiveDateLessThanEqualOrderByMinIncomeAsc(effectiveDate);
        if (brackets.isEmpty()) {
            brackets = taxBracketRepository.findByIsActiveTrueOrderByMinIncomeAsc();
        }
        if (brackets.isEmpty()) {
            return ZERO;
        }

        TaxBracket bracket = brackets.stream()
                .filter(b -> nvl(b.getMinIncome()).compareTo(assessableIncome) <= 0)
                .filter(b -> b.getMaxIncome() == null || nvl(b.getMaxIncome()).compareTo(assessableIncome) >= 0)
                .findFirst()
                .orElse(brackets.stream().max(Comparator.comparing(b -> nvl(b.getMinIncome()))).orElse(null));

        if (bracket == null || bracket.getTaxRate() == null) {
            return ZERO;
        }

        // Tax rate in database is in percentage (5, 10, 15, etc), need to divide by 100
        BigDecimal rate = BigDecimal.valueOf(bracket.getTaxRate()).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal tax = assessableIncome
                .multiply(rate)
                .subtract(nvl(bracket.getSubtractAmount()));
        return tax.max(ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal sumByType(List<SalaryAdjustment> adjustments, String type) {
        return adjustments.stream()
                .filter(a -> type.equals(a.getType()))
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal percent(BigDecimal base, BigDecimal rate) {
        return nvl(base).multiply(nvl(rate)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeRate(BigDecimal rate) {
        BigDecimal normalized = nvl(rate);
        if (normalized.compareTo(BigDecimal.ONE) > 0) {
            return normalized.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        }
        return normalized;
    }

    private BigDecimal divide(BigDecimal value, BigDecimal divisor) {
        if (divisor == null || BigDecimal.ZERO.compareTo(divisor) == 0) {
            return ZERO;
        }
        return nvl(value).divide(divisor, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal toDecimal(Float value) {
        if (value == null) {
            return ZERO;
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nvl(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}