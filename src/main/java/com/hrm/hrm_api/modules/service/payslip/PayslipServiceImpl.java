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
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.common.security.SecurityUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    private final ActionLogUtil actionLogUtil;
    private final SecurityUtil securityUtil;

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
            InsuranceConfigRepository insuranceConfigRepository,
            ActionLogUtil actionLogUtil,
            SecurityUtil securityUtil

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
        this.actionLogUtil = actionLogUtil;
        this.securityUtil = securityUtil;
    }

    /**
     * Tìm kiếm bảng lương theo bộ lọc
     */
    @Override
    public Page<Payslip> search(java.util.Map<String, Object> filters, Pageable pageable) {
        Specification<Payslip> spec = buildSpec(filters);
        return payslipRepository.findAll(spec, pageable);
    }

    /**
     * Xây dựng Specification cho tìm kiếm bảng lương
     */
    private Specification<Payslip> buildSpec(java.util.Map<String, Object> filters) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction(); // Start with true

            if (filters != null) {
                String employeeName = (String) filters.getOrDefault("employeeName", "");
                String payrollPeriodId = (String) filters.getOrDefault("payrollPeriodId", "");
                String status = (String) filters.getOrDefault("status", "");

                if (employeeName != null && !employeeName.isEmpty()) {
                    predicates = cb.and(predicates, cb.like(
                            cb.lower(root.get("employee").get("name")),
                            "%" + employeeName.toLowerCase() + "%"));
                }

                if (payrollPeriodId != null && !payrollPeriodId.isEmpty()) {
                    try {
                        UUID periodId = UUID.fromString(payrollPeriodId);
                        predicates = cb.and(predicates, cb.equal(root.get("payrollPeriod").get("id"), periodId));
                    } catch (IllegalArgumentException e) {
                        // Invalid UUID format, ignore filter
                    }
                }

                if (status != null && !status.isEmpty()) {
                    predicates = cb.and(predicates, cb.equal(root.get("status"), status));
                }
            }

            // Luôn lọc các bảng lương chưa bị xóa
            predicates = cb.and(predicates,
                    cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));

            return predicates;
        };
    }

    /**
     * Lấy tất cả bảng lương với phân trang
     */
    @Override
    public Payslip findById(UUID id) {
        return payslipRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Payslip.class, id));
    }

    @Override
    public Payslip findByEmployeeAndPayrollPeriod(UUID employeeId, UUID payrollPeriodId) {
        return payslipRepository.findByPayrollPeriodAndEmployeeAndApprovedStatus(payrollPeriodId, employeeId, false);
    }

    @Override
    @Transactional
    public Payslip update(UUID id, Payslip entity) {
        findById(id);
        entity.setId(id);

        actionLogUtil.logUpdate(
                id,
                "PAYSLIP",
                "Cập nhật bảng lương nhân viên: " + entity.getEmployee().getEmployeeCode(),
                securityUtil.getCurrentUsername());

        return payslipRepository.save(entity);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Payslip payslip = findById(id);
        payslip.setIsDeleted(true);
        payslipRepository.save(payslip);

        actionLogUtil.logDeactivate(
                id,
                "PAYSLIP",
                "Ngừng hoạt động bảng lương nhân viên: " + payslip.getEmployee().getEmployeeCode(),
                securityUtil.getCurrentUsername());
    }

    @Override
    @Transactional
    public void activate(UUID id) {
        Payslip payslip = findById(id);
        payslip.setIsDeleted(false);
        payslipRepository.save(payslip);

        actionLogUtil.logActivate(
                id,
                "PAYSLIP",
                "Kích hoạt bảng lương nhân viên: " + payslip.getEmployee().getEmployeeCode(),
                securityUtil.getCurrentUsername());
    }

    @Override
    @Transactional
    public void approve(UUID id) {
        Payslip payslip = findById(id);
        payslip.setStatus(Payslip.Status.APPROVED);
        payslipRepository.save(payslip);

        actionLogUtil.logUpdate(
                id,
                "PAYSLIP",
                "Duyệt bảng lương nhân viên: " + payslip.getEmployee().getEmployeeCode(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Tạo bảng lương cho một nhân viên cụ thể trong một kỳ lương
     * Yêu cầu:
     * - Kỳ lương phải được duyệt (APPROVED) và không bị xóa (isDeleted=false)
     * - Tổng hợp công phải được duyệt (APPROVED) và không bị xóa (isDeleted=false)
     * - Hợp đồng phải hoạt động (ACTIVE) và không bị xóa (isDeleted=false)
     * - Phụ cấp phải không bị xóa (isDeleted=false)
     * - Điều chỉnh lương phải được duyệt (APPROVED) và không bị xóa
     * (isDeleted=false)
     * - Người phụ thuộc phải đủ điều kiện (isQualified=true) và không bị xóa
     * (isDeleted=false)
     */
    @Override
    @Transactional
    public Payslip generateForEmployee(UUID payrollPeriodId, UUID employeeId) {
        // Kiểm tra payslip đã tồn tại chưa - MUST BE FIRST
        Payslip existingPayslip = payslipRepository.findByPayrollPeriodAndEmployeeAndIsDeleted(payrollPeriodId,
                employeeId, false);
        if (existingPayslip != null) {
            throw new IllegalStateException(
                    "Payslip đã tồn tại cho nhân viên này trong kỳ lương này. Vui lòng cập nhật thay vì tạo mới.");
        }

        // Kỳ lương
        PayrollPeriod period = payrollPeriodRepository
                .findByIdAndStatusAndIsDeleted(payrollPeriodId, PayrollPeriod.Status.APPROVED, false)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy kỳ lương hoặc kỳ lương chưa được duyệt: " + payrollPeriodId));

        List<AttendanceSummary> summaries = attendanceSummaryRepository
                .findByPayrollPeriodIdAndStatusAndIsDeleted(payrollPeriodId, AttendanceSummary.Status.APPROVED, false);
        AttendanceSummary summary = summaries.stream()
                .filter(s -> s.getEmployee() != null && s.getEmployee().getId().equals(employeeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tìm thấy tổng hợp công cho nhân viên: " + employeeId));

        if (summary.getEmployee() == null) {
            throw new IllegalArgumentException("Dữ liệu nhân viên không hợp lệ trong tổng hợp công");
        }

        LocalDate calculationDate = period.getEndDate() != null ? period.getEndDate() : LocalDate.now();
        Optional<Contract> contractOpt = findEffectiveContract(employeeId, calculationDate);

        if (contractOpt.isEmpty()) {
            throw new IllegalArgumentException(
                    "Không tìm thấy hợp đồng lao động có hiệu lực cho nhân viên: " + employeeId);
        }
        Contract contract = contractOpt.get();

        // Giảm trừ người phụ thuộc từ system settings
        BigDecimal dependentDeductionUnit = getSettingValue("PHU_CAP_NGUOI_PHU_THUOC", calculationDate);

        // Lương gross
        BigDecimal grossSalary = nvl(contract.getActualSalary());
        BigDecimal standardDays = toDecimal(
                period.getStandardWorkingDays() == null ? 26F : period.getStandardWorkingDays());
        BigDecimal actualDays = toDecimal(
                summary.getActualWorkingDays() == null ? standardDays.floatValue() : summary.getActualWorkingDays());

        // Lương theo ngày công = Lương cơ bản x (Công thực tế / Công chuẩn)
        BigDecimal salaryByWorkdays = divide(grossSalary.multiply(actualDays), standardDays);

        // Phụ cấp hợp đồng
        List<ContractAllowance> allowances = contractAllowanceRepository.findByContractIdAndIsDeleted(contract.getId(),
                false);

        // Tính thuế và bảo hiểm
        BigDecimal taxableAndInsurableAllowance = allowances.stream()
                .filter(a -> a.getAllowanceType() != null
                        && Boolean.TRUE.equals(a.getAllowanceType().getIsTaxable())
                        && Boolean.TRUE.equals(a.getAllowanceType().getIsInsuranceBased()))
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add);
        // Tính thuế và không tính bảo hiểm
        BigDecimal taxableAndNonInsurableAllowance = allowances.stream()
                .filter(a -> a.getAllowanceType() != null
                        && Boolean.TRUE.equals(a.getAllowanceType().getIsTaxable())
                        && !Boolean.TRUE.equals(a.getAllowanceType().getIsInsuranceBased()))
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add);
        // không Tính thuế và tính bảo hiểm
        BigDecimal nonTaxableAndInsurableAllowance = allowances.stream()
                .filter(a -> a.getAllowanceType() != null
                        && !Boolean.TRUE.equals(a.getAllowanceType().getIsTaxable())
                        && Boolean.TRUE.equals(a.getAllowanceType().getIsInsuranceBased()))
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add);
        // không Tính thuế và không tính bảo hiểm
        BigDecimal nonTaxableAndNonInsurableAllowance = allowances.stream()
                .filter(a -> a.getAllowanceType() != null
                        && !Boolean.TRUE.equals(a.getAllowanceType().getIsTaxable())
                        && !Boolean.TRUE.equals(a.getAllowanceType().getIsInsuranceBased()))
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add);

        // Điều chỉnh lương
        List<SalaryAdjustment> adjustments = salaryAdjustmentRepository
                .findByPayrollPeriodIdAndEmployeeIdAndStatusAndIsDeleted(payrollPeriodId, employeeId,
                        SalaryAdjustment.Status.APPROVED, false);
        BigDecimal bonusTax = sumByType(adjustments, SalaryAdjustment.Type.BONUS.name());
        BigDecimal bonusNonTax = sumByType(adjustments, SalaryAdjustment.Type.BONUS.name());
        BigDecimal fineTax = sumByType(adjustments, SalaryAdjustment.Type.FINE.name());
        BigDecimal fineNonTax = sumByType(adjustments, SalaryAdjustment.Type.FINE.name());
        BigDecimal advanceTax = sumByType(adjustments, SalaryAdjustment.Type.ADVANCE.name());
        BigDecimal advanceNonTax = sumByType(adjustments, SalaryAdjustment.Type.ADVANCE.name());
        // Tổng điều chỉnh tính thuế = Thưởng - phạt - ứng
        BigDecimal taxableAdjustment = bonusTax.subtract(fineTax).subtract(advanceTax);
        // Tổng điều chỉnh không tính thuế = Thưởng - phạt - ứng
        BigDecimal nonTaxableAdjustment = bonusNonTax.subtract(fineNonTax).subtract(advanceNonTax);

        long dependentCount = dependentRepository.countByEmployeeIdAndIsQualifiedTrueAndIsDeletedFalse(employeeId);
        BigDecimal deductionDependents = dependentDeductionUnit.multiply(BigDecimal.valueOf(dependentCount));

        // Tổng thu nhập chịu thuế
        BigDecimal taxableIncome = salaryByWorkdays
                .add(taxableAndInsurableAllowance)
                .add(taxableAndNonInsurableAllowance)
                .add(taxableAdjustment)
                .max(ZERO);

         // Lương tính bảo hiểm = Lương cơ bản + Phụ cấp tính bảo hiểm
        BigDecimal bhxhSalaryBase = nvl(contract.getBaseSalary()).add(taxableAndInsurableAllowance)
                .add(nonTaxableAndInsurableAllowance);
        BigDecimal socialRate = getInsuranceEmployeeRate("BHXH", calculationDate);
        BigDecimal healthRate = getInsuranceEmployeeRate("BHYT", calculationDate);
        BigDecimal unemploymentRate = getInsuranceEmployeeRate("BHTN", calculationDate);
        BigDecimal bhxhAmount = percent(bhxhSalaryBase, socialRate);
        BigDecimal bhytAmount = percent(bhxhSalaryBase, healthRate);
        BigDecimal bhtnAmount = percent(bhxhSalaryBase, unemploymentRate);
        // Tổng bảo hiểm
        BigDecimal totalInsurance = bhxhAmount.add(bhytAmount).add(bhtnAmount);

        // Giảm trừ cá nhân: Mỗi người 11 triệu/tháng
        // Hoặc lấy từ system settings nếu có
        BigDecimal personalDeduction = getSettingValue("GIAM_TRU_CA_NHAN", calculationDate);
        if (personalDeduction.compareTo(ZERO) <= 0) {
            // Mặc định: 11 triệu/tháng
            personalDeduction = BigDecimal.valueOf(11_000_000).setScale(2, RoundingMode.HALF_UP);
        }

        // Tổng giảm trừ = Giảm trừ cá nhân + Giảm trừ người phụ thuộc + Tổng bảo hiểm
        BigDecimal totalDeductions = personalDeduction
                .add(deductionDependents)
                .add(totalInsurance);

        // Tổng thu nhập tính thuế = Thu nhập chịu thuế - Tổng giảm trừ
        BigDecimal assessableIncome = taxableIncome
                .subtract(totalDeductions)
                .max(ZERO);

        // Giảm trừ cố định
        BigDecimal taxFixedDeduction = getSubtract(assessableIncome, calculationDate);

        // Thuế TNCN = (Thu nhập tính thuế x thuế suất) - Giảm trừ cố định
        BigDecimal taxAmount = calculatePit(assessableIncome, calculationDate).subtract(taxFixedDeduction).max(ZERO);

        BigDecimal netSalary = taxableIncome
                .add(nonTaxableAndInsurableAllowance)
                .add(nonTaxableAndNonInsurableAllowance)
                .add(nonTaxableAdjustment)
                .subtract(taxAmount)
                .setScale(2, RoundingMode.HALF_UP);

        // Tạo payslip mới (KHÔNG dùng orElseGet)
        Payslip payslip = new Payslip();
        payslip.setPayrollPeriod(period);
        payslip.setEmployee(summary.getEmployee()); // Nhân viên
        payslip.setContract(contract); // Hợp đồng
        payslip.setSalaryByWorkdays(salaryByWorkdays); // Lương thanh toán theo công
        payslip.setWorkdays(actualDays); // Công thực tế
        payslip.setTotalWorkdays(standardDays); // Tổng số ngày công trong kỳ
        payslip.setGrossSalary(grossSalary); // Lương cơ sở
        payslip.setBhxhSalaryBase(bhxhSalaryBase); // Lương tính bảo hiểm
        payslip.setTaxableAndInsurableAllowance(taxableAndInsurableAllowance);// Phụ cấp chịu thuế và tính bảo hiểm
        payslip.setTaxableAndNonInsurableAllowance(taxableAndNonInsurableAllowance); // Phụ cấp chịu thuế và không tính bảo hiểm
        payslip.setNonTaxableAndInsurableAllowance(nonTaxableAndInsurableAllowance); // Phụ cấp không chịu thuế và tính bảo hiểm
        payslip.setNonTaxableAndNonInsurableAllowance(nonTaxableAndNonInsurableAllowance); // Phụ cấp không chịu thuế và không tính bảo hiểm
        payslip.setTotalBonus(taxableAdjustment); // Tổng thưởng/phạt tính thuế
        payslip.setTotalBonusNonTax(nonTaxableAdjustment); // Tổng thưởng/phạt không tính thuế
        payslip.setBhxhAmount(bhxhAmount); // Bảo hiểm xã hội (8%)
        payslip.setBhytAmount(bhytAmount); // Bảo hiểm y tế (1.5%)
        payslip.setBhtnAmount(bhtnAmount); // Bảo hiểm thất nghiệp (1%)
        payslip.setTaxableIncome(taxableIncome); // Thu nhập chịu thuế
        payslip.setDeductionPersonal(personalDeduction); // Giảm trừ cá nhân
        payslip.setDeductionDependents(deductionDependents); // Giảm trừ người phụ thuộc
        payslip.setTaxFixedDeduction(taxFixedDeduction); // Giảm trừ cố định
        payslip.setAssessableIncome(assessableIncome); // Thu nhập tính thuế
        payslip.setTotalDeductions(totalDeductions); // Tổng giảm trừ
        payslip.setTaxAmount(taxAmount); // Thuế TNCN
        payslip.setNetSalary(netSalary); // Lương thực nhận
        payslip.setCreatedBy(securityUtil.getCurrentUsername()); // Người tạo bảng lương

        Payslip savedPayslip = payslipRepository.save(payslip);

        actionLogUtil.logCreate(
                savedPayslip.getId(),
                "PAYSLIP",
                "Tạo bảng lương mới cho nhân viên: " + savedPayslip.getEmployee().getEmployeeCode(),
                securityUtil.getCurrentUsername());

        return savedPayslip;
    }

    /**
     * Tìm hợp đồng lao động có hiệu lực cho nhân viên vào ngày cụ thể
     * Chỉ lấy hợp đồng đã được duyệt (ACTIVE), không bị xóa (isDeleted=false)
     * và có ngày bắt đầu <= ngày tính toán <= ngày kết thúc (hoặc không có ngày kết
     * thúc)
     */
    private Optional<Contract> findEffectiveContract(UUID employeeId, LocalDate date) {
        return contractRepository
                .findByEmployeeIdAndStatusAndStartDateLessThanEqualAndIsDeletedFalseOrderByStartDateDesc(employeeId,
                        "ACTIVE", date)
                .stream()
                .filter(c -> Boolean.FALSE.equals(c.getIsDeleted()))
                .filter(c -> c.getEndDate() == null || !c.getEndDate().isBefore(date))
                .findFirst();
    }

    private BigDecimal getSettingValue(String key, LocalDate effectiveDate) {
        return sysSettingRepository
                .findTopByKeyAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(key, effectiveDate)
                .map(SysSetting::getValue)
                .map(this::nvl)
                .orElse(ZERO);
    }

    private BigDecimal getInsuranceEmployeeRate(String type, LocalDate effectiveDate) {
        BigDecimal rate = insuranceConfigRepository
                .findTopByTypeAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(type, effectiveDate)
                .map(InsuranceConfig::getEmployeeRate)
                .map(this::nvl)
                .orElse(ZERO);
        return normalizeRate(rate);
    }

    private BigDecimal calculatePit(BigDecimal assessableIncome, LocalDate effectiveDate) {
        List<TaxBracket> brackets = taxBracketRepository
                .findByIsActiveTrueAndEffectiveDateLessThanEqualOrderByMinIncomeAsc(effectiveDate);
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
        BigDecimal rate = BigDecimal.valueOf(bracket.getTaxRate()).divide(BigDecimal.valueOf(100), 6,
                RoundingMode.HALF_UP);
        BigDecimal tax = assessableIncome
                .multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
        return tax.max(ZERO);
    }

    // Lấy tiền miễn trừ cố định trong thuế dựa vào thu nhập tính thuế
    private BigDecimal getSubtract(BigDecimal assessableIncome, LocalDate effectiveDate) {
        List<TaxBracket> brackets = taxBracketRepository
                .findByIsActiveTrueAndEffectiveDateLessThanEqualOrderByMinIncomeAsc(effectiveDate);
        if (brackets.isEmpty()) {
            brackets = taxBracketRepository.findByIsActiveTrueOrderByMinIncomeAsc();
        }
        if (brackets.isEmpty()) {
            return ZERO;
        }

        // Tìm bậc thuế phù hợp với thu nhập tính thuế
        TaxBracket bracket = brackets.stream()
                .filter(b -> nvl(b.getMinIncome()).compareTo(assessableIncome) <= 0)
                .filter(b -> b.getMaxIncome() == null || nvl(b.getMaxIncome()).compareTo(assessableIncome) >= 0)
                .findFirst()
                .orElse(brackets.stream().max(Comparator.comparing(b -> nvl(b.getMinIncome()))).orElse(null));

        if (bracket == null) {
            return ZERO;
        }

        return nvl(bracket.getSubtractAmount());
    }

    private BigDecimal sumByType(List<SalaryAdjustment> adjustments, String type) {
        return adjustments.stream()
                .filter(a -> a.getType() != null && type.equals(a.getType().name()))
                .map(a -> nvl(a.getAmount()))
                .reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal percent(BigDecimal base, BigDecimal rate) {
        return nvl(base).multiply(nvl(rate)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeRate(BigDecimal rate) {
        BigDecimal normalized = nvl(rate);
        // Database lưu tỷ lệ dưới dạng phần trăm (1, 2, 8, ...)
        // Nếu >= 1, chia cho 100 để chuyển thành decimal (0.01, 0.02, 0.08, ...)
        if (normalized.compareTo(BigDecimal.ONE) >= 0) {
            return normalized.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        }
        // Nếu đã là decimal (0 < x < 1), return as is
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