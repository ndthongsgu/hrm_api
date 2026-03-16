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
import com.hrm.hrm_api.modules.service.AbstractCrudService;
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
public class PayslipServiceImpl extends AbstractCrudService<Payslip> implements PayslipService {

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
        super(repository, Payslip.class);
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

            BigDecimal standardDays = toDecimal(period.getStandardWorkingDays() == null ? 26F : period.getStandardWorkingDays());
            BigDecimal actualDays = toDecimal(summary.getActualWorkingDays() == null ? standardDays.floatValue() : summary.getActualWorkingDays());
            long dependentCount = dependentRepository.countByEmployeeIdAndIsQualifiedTrue(employeeId);
            BigDecimal deductionDependents = dependentDeductionUnit.multiply(BigDecimal.valueOf(dependentCount));

            BigDecimal grossSalary = resolveGrossSalary(
                    contract,
                    standardDays,
                    actualDays,
                    personalDeduction,
                    deductionDependents,
                    calculationDate
            );

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

    private Optional<Contract> findEffectiveContract(UUID employeeId, LocalDate date) {
        return contractRepository.findByEmployeeIdAndStatusAndStartDateLessThanEqualOrderByStartDateDesc(employeeId, "ACTIVE", date)
                .stream()
                .filter(c -> c.getEndDate() == null || !c.getEndDate().isBefore(date))
                .findFirst();
    }

    private BigDecimal resolveGrossSalary(
            Contract contract,
            BigDecimal standardDays,
            BigDecimal actualDays,
            BigDecimal personalDeduction,
            BigDecimal deductionDependents,
            LocalDate calculationDate
    ) {
        BigDecimal contractSalary = nvl(contract.getActualSalary());
        if (!Boolean.TRUE.equals(contract.getIsNet())) {
            return contractSalary;
        }

        BigDecimal targetNetByWorkdays = divide(contractSalary.multiply(actualDays), standardDays);
        BigDecimal totalInsurance = calculateTotalInsurance(contract, calculationDate);

        BigDecimal low = ZERO;
        BigDecimal high = contractSalary.max(BigDecimal.ONE);

        for (int i = 0; i < 20; i++) {
            BigDecimal netAtHigh = calculateNetByWorkdaysFromGross(high, standardDays, actualDays, totalInsurance, personalDeduction, deductionDependents, calculationDate);
            if (netAtHigh.compareTo(targetNetByWorkdays) >= 0) {
                break;
            }
            high = high.multiply(BigDecimal.valueOf(2));
        }

        for (int i = 0; i < 40; i++) {
            BigDecimal mid = low.add(high).divide(BigDecimal.valueOf(2), 6, RoundingMode.HALF_UP);
            BigDecimal netAtMid = calculateNetByWorkdaysFromGross(mid, standardDays, actualDays, totalInsurance, personalDeduction, deductionDependents, calculationDate);
            if (netAtMid.compareTo(targetNetByWorkdays) >= 0) {
                high = mid;
            } else {
                low = mid;
            }
        }

        return high.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalInsurance(Contract contract, LocalDate calculationDate) {
        BigDecimal baseSalary = nvl(contract.getBaseSalary());
        BigDecimal bhxhBase = calculateInsuranceBase(baseSalary, "SOCIAL", calculationDate);
        BigDecimal socialRate = getInsuranceEmployeeRate("SOCIAL", calculationDate);
        BigDecimal healthRate = getInsuranceEmployeeRate("HEALTH", calculationDate);
        BigDecimal unemploymentRate = getInsuranceEmployeeRate("UNEMPLOYMENT", calculationDate);

        BigDecimal bhxhAmount = percent(bhxhBase, socialRate);
        BigDecimal bhytAmount = percent(bhxhBase, healthRate);
        BigDecimal bhtnAmount = percent(bhxhBase, unemploymentRate);
        return bhxhAmount.add(bhytAmount).add(bhtnAmount);
    }

    private BigDecimal calculateNetByWorkdaysFromGross(
            BigDecimal grossSalary,
            BigDecimal standardDays,
            BigDecimal actualDays,
            BigDecimal totalInsurance,
            BigDecimal personalDeduction,
            BigDecimal deductionDependents,
            LocalDate calculationDate
    ) {
        BigDecimal salaryByWorkdays = divide(grossSalary.multiply(actualDays), standardDays);
        BigDecimal taxableIncome = salaryByWorkdays.subtract(totalInsurance).max(ZERO);
        BigDecimal assessableIncome = taxableIncome
                .subtract(personalDeduction)
                .subtract(deductionDependents)
                .max(ZERO);
        BigDecimal taxAmount = calculatePit(assessableIncome, calculationDate);
        return salaryByWorkdays.subtract(totalInsurance).subtract(taxAmount).setScale(2, RoundingMode.HALF_UP);
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

        BigDecimal tax = assessableIncome
                .multiply(BigDecimal.valueOf(bracket.getTaxRate()))
                .subtract(nvl(bracket.getSubtractAmount()));
        return tax.max(ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal sumByType(List<SalaryAdjustment> adjustments, String type) {
        return adjustments.stream()
                .filter(a -> type.equalsIgnoreCase(a.getType()))
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