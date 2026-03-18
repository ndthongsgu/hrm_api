package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "payslips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payslip extends BaseEntity {
    // Inherit id, timestamps and audit fields from BaseEntity

    @ManyToOne
    @JoinColumn(name = "payroll_period_id")
    private PayrollPeriod payrollPeriod; // Kỳ lương

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee; // Nhân viên

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract; // Hợp đồng

    // 1. Income - DOANH THU
    @Column(precision = 15, scale = 2)
    private BigDecimal grossSalary; // Lương cơ bản

    @Column(precision = 15, scale = 2)
    private BigDecimal salaryByWorkdays; // Lương theo ngày công

    @Column(precision = 15, scale = 2)
    private BigDecimal totalAllowance; // Tổng phụ cấp

    @Column(precision = 15, scale = 2)
    private BigDecimal totalBonus; // Tổng thưởng (thưởng - phạt)

    // 2. Insurance - BẢO HIỂM
    @Column(precision = 15, scale = 2)
    private BigDecimal bhxhSalaryBase; // Căn cứ tính BHXH

    @Column(precision = 15, scale = 2)
    private BigDecimal bhxhAmount; // Bảo hiểm xã hội (8%)

    @Column(precision = 15, scale = 2)
    private BigDecimal bhytAmount; // Bảo hiểm y tế (2%)

    @Column(precision = 15, scale = 2)
    private BigDecimal bhtnAmount; // Bảo hiểm thất nghiệp (1%)

    @Column(precision = 15, scale = 2)
    private BigDecimal socialInsuranceBenefitAmount; // Trợ cấp BHXH

    // 3. Tax - THUẾ
    @Column(precision = 15, scale = 2)
    private BigDecimal taxableIncome; // Thu nhập chịu thuế

    @Column(precision = 15, scale = 2)
    private BigDecimal deductionPersonal; // Giảm trừ cá nhân

    @Column(precision = 15, scale = 2)
    private BigDecimal deductionDependents; // Giảm trừ người phụ thuộc

    @Column(precision = 15, scale = 2)
    private BigDecimal assessableIncome; // Thu nhập tính thuế

    @Column(precision = 15, scale = 2)
    private BigDecimal taxAmount; // Tiền thuế TNCN

    // 4. Net - THỰC NHÂN
    @Column(precision = 15, scale = 2)
    private BigDecimal netSalary; // Lương ròng (thực nhận)

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING; // Trạng thái (PENDING/APPROVED)

    public enum Status {
        APPROVED, PENDING
    }
}