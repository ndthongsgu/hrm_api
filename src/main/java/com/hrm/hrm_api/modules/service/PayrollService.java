package com.hrm.hrm_api.modules.service;

import com.hrm.hrm_api.modules.entity.*;
import com.hrm.hrm_api.modules.repository.AttendanceRepository;
import com.hrm.hrm_api.modules.repository.PayrollRepository;
import com.hrm.hrm_api.modules.repository.SalaryStructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private SalaryStructureRepository salaryStructureRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Payroll> getAllPayrolls() {
        return payrollRepository.findAll();
    }

    public Optional<Payroll> getPayrollById(Long id) {
        return payrollRepository.findById(id);
    }

    public Payroll savePayroll(Payroll payroll) {
        return payrollRepository.save(payroll);
    }

    public void deletePayroll(Long id) {
        payrollRepository.deleteById(id);
    }

    // Calculate payroll for employee in a month
    public Payroll calculatePayroll(Long employeeId, int month, int year) {
        Optional<Payroll> existing = payrollRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Get salary structure
        SalaryStructure structure = salaryStructureRepository.findAll().stream()
                .filter(s -> s.getEmployee().getId().equals(employeeId))
                .findFirst().orElseThrow(() -> new RuntimeException("Salary structure not found"));

        // Get attendances for the month
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, start, end);

        int workingDays = attendances.size(); // Assume each attendance is a working day
        BigDecimal dailySalary = structure.getBasicSalary().divide(BigDecimal.valueOf(30), RoundingMode.HALF_UP); // Approximate
        BigDecimal grossSalary = dailySalary.multiply(BigDecimal.valueOf(workingDays))
                .add(structure.getHousingAllowance())
                .add(structure.getTransportAllowance())
                .add(structure.getOtherAllowances());

        // Deductions
        BigDecimal bhxh = grossSalary.multiply(structure.getBhxhRate());
        BigDecimal bhyt = grossSalary.multiply(structure.getBhytRate());
        BigDecimal bhtn = grossSalary.multiply(structure.getBhtnRate());

        // Tax calculation (simplified)
        BigDecimal taxableIncome = grossSalary.subtract(bhxh).subtract(bhyt).subtract(bhtn);
        BigDecimal tax = calculateTax(taxableIncome);

        BigDecimal netSalary = grossSalary.subtract(bhxh).subtract(bhyt).subtract(bhtn).subtract(tax);

        Payroll payroll = new Payroll();
        payroll.setEmployee(structure.getEmployee());
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setGrossSalary(grossSalary);
        payroll.setBhxhDeduction(bhxh);
        payroll.setBhytDeduction(bhyt);
        payroll.setBhtnDeduction(bhtn);
        payroll.setTaxDeduction(tax);
        payroll.setNetSalary(netSalary);
        payroll.setWorkingDays(workingDays);

        return savePayroll(payroll);
    }

    private BigDecimal calculateTax(BigDecimal taxableIncome) {
        // Simplified Vietnam tax calculation
        if (taxableIncome.compareTo(BigDecimal.valueOf(5000000)) <= 0) {
            return taxableIncome.multiply(BigDecimal.valueOf(0.05));
        } else if (taxableIncome.compareTo(BigDecimal.valueOf(10000000)) <= 0) {
            return taxableIncome.multiply(BigDecimal.valueOf(0.10));
        } else {
            return taxableIncome.multiply(BigDecimal.valueOf(0.20));
        }
    }
}
