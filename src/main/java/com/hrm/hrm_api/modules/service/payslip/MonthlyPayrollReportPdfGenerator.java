package com.hrm.hrm_api.modules.service.payslip;

import com.hrm.hrm_api.common.exception.PdfGenerationException;
import com.hrm.hrm_api.modules.entity.Payslip;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MonthlyPayrollReportPdfGenerator {

    private static final String DEFAULT_FONT = "fonts/NotoSans-Regular.ttf";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    MonthlyPayrollReportPdfGenerator() {
        this(DEFAULT_FONT);
    }

    private final String fontClasspathLocation;

    MonthlyPayrollReportPdfGenerator(String fontClasspathLocation) {
        this.fontClasspathLocation = fontClasspathLocation;
    }

    public byte[] generate(MonthlyPayrollReportData reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A3.rotate());
            document.setMargins(24, 24, 24, 24);

            PdfFont font = loadFont();
            document.setFont(font);

            addHeader(document, reportData);
            addSummarySection(document, reportData.payslips());
            addIncomeSection(document, reportData.payslips());
            addInsuranceSection(document, reportData.payslips());
            addTaxSection(document, reportData.payslips());
            addNetSection(document, reportData.payslips());

            document.close();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new PdfGenerationException("Không thể tạo file PDF báo cáo lương", ex);
        }
    }

    private PdfFont loadFont() throws IOException {
        ClassPathResource fontResource = new ClassPathResource(fontClasspathLocation);
        return PdfFontFactory.createFont(
                StreamUtil.inputStreamToArray(fontResource.getInputStream()),
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
        );
    }

    private void addHeader(Document document, MonthlyPayrollReportData reportData) {
        document.add(new Paragraph("BÁO CÁO LƯƠNG THÁNG")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Kỳ lương: " + defaultText(reportData.payrollPeriod().getName()))
                .setFontSize(12)
                .setMarginTop(8));
        document.add(new Paragraph(String.format(
                "Từ ngày: %s    Đến ngày: %s    Số nhân viên: %d",
                formatDate(reportData.payrollPeriod().getStartDate()),
                formatDate(reportData.payrollPeriod().getEndDate()),
                reportData.payslips().size())));
        document.add(new Paragraph(String.format(
                "Người xuất: %s    Thời điểm xuất: %s",
                defaultText(reportData.exportedBy()),
                reportData.exportedAt() == null ? "" : reportData.exportedAt().format(DATE_TIME_FORMATTER)))
                .setMarginBottom(16));
    }

    private void addSummarySection(Document document, List<Payslip> payslips) {
        document.add(sectionTitle("1. Tổng quan"));
        Table table = createTable(8, 14, 8, 8, 10, 10, 10, 10, 10, 6);
        addHeaders(table, "Mã NV", "Họ tên", "Công thực tế", "Tổng công", "Lương gross",
                "Thu nhập chịu thuế", "Tổng giảm trừ", "Thuế TNCN", "Thực nhận", "Trạng thái");

        for (Payslip payslip : payslips) {
            addTextCell(table, employeeCode(payslip));
            addTextCell(table, employeeName(payslip));
            addNumberCell(table, payslip.getWorkdays());
            addNumberCell(table, payslip.getTotalWorkdays());
            addNumberCell(table, payslip.getGrossSalary());
            addNumberCell(table, payslip.getTaxableIncome());
            addNumberCell(table, payslip.getTotalDeductions());
            addNumberCell(table, payslip.getTaxAmount());
            addNumberCell(table, payslip.getNetSalary());
            addTextCell(table, payslip.getStatus() == null ? "" : payslip.getStatus().name());
        }

        addTotalLabel(table, 2);
        addNumberCell(table, sum(payslips, Payslip::getWorkdays));
        addNumberCell(table, sum(payslips, Payslip::getTotalWorkdays));
        addNumberCell(table, sum(payslips, Payslip::getGrossSalary));
        addNumberCell(table, sum(payslips, Payslip::getTaxableIncome));
        addNumberCell(table, sum(payslips, Payslip::getTotalDeductions));
        addNumberCell(table, sum(payslips, Payslip::getTaxAmount));
        addNumberCell(table, sum(payslips, Payslip::getNetSalary));
        addTextCell(table, "");

        document.add(table);
    }

    private void addIncomeSection(Document document, List<Payslip> payslips) {
        document.add(sectionTitle("2. Thu nhập"));
        Table table = createTable(7, 13, 9, 9, 9, 9, 9, 9, 9, 9);
        addHeaders(table, "Mã NV", "Họ tên", "Lương gross", "Lương theo công",
                "PC chịu thuế + BH", "PC chịu thuế - BH", "PC miễn thuế + BH",
                "PC miễn thuế - BH", "Thưởng/phạt thuế", "Thưởng/phạt miễn thuế");

        for (Payslip payslip : payslips) {
            addTextCell(table, employeeCode(payslip));
            addTextCell(table, employeeName(payslip));
            addNumberCell(table, payslip.getGrossSalary());
            addNumberCell(table, payslip.getSalaryByWorkdays());
            addNumberCell(table, payslip.getTaxableAndInsurableAllowance());
            addNumberCell(table, payslip.getTaxableAndNonInsurableAllowance());
            addNumberCell(table, payslip.getNonTaxableAndInsurableAllowance());
            addNumberCell(table, payslip.getNonTaxableAndNonInsurableAllowance());
            addNumberCell(table, payslip.getTotalBonus());
            addNumberCell(table, payslip.getTotalBonusNonTax());
        }

        addTotalLabel(table, 2);
        addNumberCell(table, sum(payslips, Payslip::getGrossSalary));
        addNumberCell(table, sum(payslips, Payslip::getSalaryByWorkdays));
        addNumberCell(table, sum(payslips, Payslip::getTaxableAndInsurableAllowance));
        addNumberCell(table, sum(payslips, Payslip::getTaxableAndNonInsurableAllowance));
        addNumberCell(table, sum(payslips, Payslip::getNonTaxableAndInsurableAllowance));
        addNumberCell(table, sum(payslips, Payslip::getNonTaxableAndNonInsurableAllowance));
        addNumberCell(table, sum(payslips, Payslip::getTotalBonus));
        addNumberCell(table, sum(payslips, Payslip::getTotalBonusNonTax));

        document.add(table);
    }

    private void addInsuranceSection(Document document, List<Payslip> payslips) {
        document.add(sectionTitle("3. Bảo hiểm"));
        Table table = createTable(8, 14, 10, 10, 10, 10);
        addHeaders(table, "Mã NV", "Họ tên", "Căn cứ BHXH", "BHXH", "BHYT", "BHTN");

        for (Payslip payslip : payslips) {
            addTextCell(table, employeeCode(payslip));
            addTextCell(table, employeeName(payslip));
            addNumberCell(table, payslip.getBhxhSalaryBase());
            addNumberCell(table, payslip.getBhxhAmount());
            addNumberCell(table, payslip.getBhytAmount());
            addNumberCell(table, payslip.getBhtnAmount());
        }

        addTotalLabel(table, 2);
        addNumberCell(table, sum(payslips, Payslip::getBhxhSalaryBase));
        addNumberCell(table, sum(payslips, Payslip::getBhxhAmount));
        addNumberCell(table, sum(payslips, Payslip::getBhytAmount));
        addNumberCell(table, sum(payslips, Payslip::getBhtnAmount));

        document.add(table);
    }

    private void addTaxSection(Document document, List<Payslip> payslips) {
        document.add(sectionTitle("4. Thuế"));
        Table table = createTable(7, 13, 9, 9, 9, 9, 9, 9, 8, 9);
        addHeaders(table, "Mã NV", "Họ tên", "Thu nhập chịu thuế", "GT cá nhân", "GT người phụ thuộc",
                "GT cố định", "Tổng giảm trừ", "Thu nhập tính thuế", "Thuế suất", "Thuế TNCN");

        for (Payslip payslip : payslips) {
            addTextCell(table, employeeCode(payslip));
            addTextCell(table, employeeName(payslip));
            addNumberCell(table, payslip.getTaxableIncome());
            addNumberCell(table, payslip.getDeductionPersonal());
            addNumberCell(table, payslip.getDeductionDependents());
            addNumberCell(table, payslip.getTaxFixedDeduction());
            addNumberCell(table, payslip.getTotalDeductions());
            addNumberCell(table, payslip.getAssessableIncome());
            addTextCell(table, formatRate(payslip.getTaxRate()));
            addNumberCell(table, payslip.getTaxAmount());
        }

        addTotalLabel(table, 2);
        addNumberCell(table, sum(payslips, Payslip::getTaxableIncome));
        addNumberCell(table, sum(payslips, Payslip::getDeductionPersonal));
        addNumberCell(table, sum(payslips, Payslip::getDeductionDependents));
        addNumberCell(table, sum(payslips, Payslip::getTaxFixedDeduction));
        addNumberCell(table, sum(payslips, Payslip::getTotalDeductions));
        addNumberCell(table, sum(payslips, Payslip::getAssessableIncome));
        addTextCell(table, "");
        addNumberCell(table, sum(payslips, Payslip::getTaxAmount));

        document.add(table);
    }

    private void addNetSection(Document document, List<Payslip> payslips) {
        document.add(sectionTitle("5. Thực nhận"));
        Table table = createTable(10, 20, 12);
        addHeaders(table, "Mã NV", "Họ tên", "Thực nhận");

        for (Payslip payslip : payslips) {
            addTextCell(table, employeeCode(payslip));
            addTextCell(table, employeeName(payslip));
            addNumberCell(table, payslip.getNetSalary());
        }

        addTotalLabel(table, 2);
        addNumberCell(table, sum(payslips, Payslip::getNetSalary));

        document.add(table);
    }

    private Table createTable(float... widths) {
        return new Table(UnitValue.createPercentArray(widths))
                .useAllAvailableWidth()
                .setMarginBottom(16);
    }

    private void addHeaders(Table table, String... headers) {
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(0.75f)));
        }
    }

    private Paragraph sectionTitle(String text) {
        return new Paragraph(text)
                .setBold()
                .setFontSize(13)
                .setMarginTop(8)
                .setMarginBottom(8);
    }

    private void addTextCell(Table table, String value) {
        table.addCell(baseCell(defaultText(value), TextAlignment.LEFT));
    }

    private void addNumberCell(Table table, BigDecimal value) {
        table.addCell(baseCell(formatDecimal(value), TextAlignment.RIGHT));
    }

    private void addTotalLabel(Table table, int colspan) {
        table.addCell(new Cell(1, colspan)
                .add(new Paragraph("Tổng").setBold())
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(0.75f)));
    }

    private Cell baseCell(String value, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(value))
                .setTextAlignment(alignment)
                .setBorder(new SolidBorder(0.75f));
    }

    private BigDecimal sum(List<Payslip> payslips, java.util.function.Function<Payslip, BigDecimal> extractor) {
        return payslips.stream()
                .map(extractor)
                .map(this::nvl)
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal::add);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String defaultText(String value) {
        return value == null ? "" : value;
    }

    private String employeeCode(Payslip payslip) {
        return payslip.getEmployee() == null ? "" : defaultText(payslip.getEmployee().getEmployeeCode());
    }

    private String employeeName(Payslip payslip) {
        return payslip.getEmployee() == null ? "" : defaultText(payslip.getEmployee().getFullName());
    }

    private String formatDecimal(BigDecimal value) {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        return format.format(nvl(value));
    }

    private String formatRate(BigDecimal value) {
        if (value == null) {
            return "";
        }
        return formatDecimal(value) + "%";
    }

    private String formatDate(java.time.LocalDate value) {
        return value == null ? "" : value.format(DATE_FORMATTER);
    }
}
