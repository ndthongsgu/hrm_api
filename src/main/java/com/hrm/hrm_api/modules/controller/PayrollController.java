package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.entity.Payroll;
import com.hrm.hrm_api.modules.service.PayrollService;
import com.hrm.hrm_api.modules.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private PdfService pdfService;

    @GetMapping
    public List<Payroll> getAllPayrolls() {
        return payrollService.getAllPayrolls();
    }

    @GetMapping("/{id}")
    public Payroll getPayrollById(@PathVariable Long id) {
        return payrollService.getPayrollById(id).orElse(null);
    }

    @PostMapping
    public Payroll createPayroll(@RequestBody Payroll payroll) {
        return payrollService.savePayroll(payroll);
    }

    @PutMapping("/{id}")
    public Payroll updatePayroll(@PathVariable Long id, @RequestBody Payroll payroll) {
        payroll.setId(id);
        return payrollService.savePayroll(payroll);
    }

    @DeleteMapping("/{id}")
    public void deletePayroll(@PathVariable Long id) {
        payrollService.deletePayroll(id);
    }

    @GetMapping("/calculate/{employeeId}/{month}/{year}")
    public Payroll calculatePayroll(@PathVariable Long employeeId, @PathVariable int month, @PathVariable int year) {
        return payrollService.calculatePayroll(employeeId, month, year);
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> downloadSalarySlip(@PathVariable Long id) {
        Payroll payroll = payrollService.getPayrollById(id).orElse(null);
        if (payroll == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdfBytes = pdfService.generateSalarySlip(payroll);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "salary_slip_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
