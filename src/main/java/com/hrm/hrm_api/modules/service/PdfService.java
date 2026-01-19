package com.hrm.hrm_api.modules.service;

import com.hrm.hrm_api.modules.entity.Payroll;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateSalarySlip(Payroll payroll) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Salary Slip"));
        document.add(new Paragraph("Employee: " + payroll.getEmployee().getName()));
        document.add(new Paragraph("Month: " + payroll.getMonth() + "/" + payroll.getYear()));

        Table table = new Table(2);
        table.addCell("Gross Salary");
        table.addCell(payroll.getGrossSalary().toString());
        table.addCell("BHXH Deduction");
        table.addCell(payroll.getBhxhDeduction().toString());
        table.addCell("BHYT Deduction");
        table.addCell(payroll.getBhytDeduction().toString());
        table.addCell("BHTN Deduction");
        table.addCell(payroll.getBhtnDeduction().toString());
        table.addCell("Tax Deduction");
        table.addCell(payroll.getTaxDeduction().toString());
        table.addCell("Net Salary");
        table.addCell(payroll.getNetSalary().toString());

        document.add(table);
        document.close();

        return baos.toByteArray();
    }
}
