# Monthly Payroll PDF Report Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a payroll export endpoint that returns a downloadable monthly HR PDF report for one approved payroll period using approved payslips only.

**Architecture:** Extend the existing payslip module with an export use case in `PayslipService`, validate the payroll period against the same approval and soft-delete rules already used by payroll operations, fetch approved payslips in deterministic order, and delegate PDF rendering to a dedicated iText-based generator that embeds a bundled Unicode font. Return a small export result object to the controller so filename generation and PDF bytes stay consistent and testable.

**Tech Stack:** Java 17, Spring Boot 3.5, Spring MVC, Spring Data JPA, Spring Security + `@RequirePermission`, iText 7, JUnit 5, Mockito, MockMvc, H2

---

## File Map

- Modify: `src/main/java/com/hrm/hrm_api/modules/repository/PayslipRepository.java`
  Purpose: add a deterministic export query for approved, non-deleted payslips in one payroll period.

- Modify: `src/main/java/com/hrm/hrm_api/modules/service/payslip/PayslipService.java`
  Purpose: expose the monthly export use case to the controller.

- Modify: `src/main/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImpl.java`
  Purpose: validate payroll period eligibility, fetch export rows, call the PDF generator, and return export metadata.

- Modify: `src/main/java/com/hrm/hrm_api/modules/controller/PayslipController.java`
  Purpose: add the `/api/payslips/export/monthly-report/{payrollPeriodId}` endpoint and wire download headers.

- Modify: `src/main/java/com/hrm/hrm_api/common/exception/GlobalExceptionHandler.java`
  Purpose: return a sanitized server error when PDF generation fails.

- Create: `src/main/java/com/hrm/hrm_api/common/exception/PdfGenerationException.java`
  Purpose: represent internal PDF rendering failures without leaking low-level iText errors.

- Create: `src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGenerator.java`
  Purpose: generate the grouped PDF document with embedded Unicode font support.

- Create: `src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportData.java`
  Purpose: carry payroll period data, approved payslips, export timestamp, and exported-by username into the PDF generator.

- Create: `src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportExport.java`
  Purpose: carry `byte[] content`, ASCII fallback filename, and UTF-8 filename from service to controller.

- Create: `src/main/resources/fonts/NotoSans-Regular.ttf`
  Purpose: bundled Unicode-capable font used by the PDF generator for Vietnamese text.

- Create: `src/test/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImplTest.java`
  Purpose: unit-test payroll period validation, empty-data behavior, and export orchestration.

- Create: `src/test/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGeneratorTest.java`
  Purpose: verify PDF generation with Unicode text and sanitized failure when font loading breaks.

- Create: `src/test/java/com/hrm/hrm_api/modules/controller/PayslipControllerExportTest.java`
  Purpose: verify the export endpoint, permission handling, content type, and `Content-Disposition` header behavior.

## Chunk 1: Export Core

### Task 1: Define the Export Contract and Service Validation

**Files:**
- Create: `src/main/java/com/hrm/hrm_api/common/exception/PdfGenerationException.java`
- Create: `src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportData.java`
- Create: `src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGenerator.java`
- Create: `src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportExport.java`
- Create: `src/test/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImplTest.java`
- Modify: `src/main/java/com/hrm/hrm_api/modules/service/payslip/PayslipService.java`
- Modify: `src/main/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImpl.java`
- Modify: `src/main/java/com/hrm/hrm_api/modules/repository/PayslipRepository.java`
- Test: `src/test/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImplTest.java`

- [ ] **Step 1: Write the failing service tests**

```java
@ExtendWith(MockitoExtension.class)
class PayslipServiceImplTest {

    @Mock private PayslipRepository payslipRepository;
    @Mock private PayrollPeriodRepository payrollPeriodRepository;
    @Mock private AttendanceSummaryRepository attendanceSummaryRepository;
    @Mock private ContractRepository contractRepository;
    @Mock private ContractAllowanceRepository contractAllowanceRepository;
    @Mock private SalaryAdjustmentRepository salaryAdjustmentRepository;
    @Mock private DependentRepository dependentRepository;
    @Mock private SysSettingRepository sysSettingRepository;
    @Mock private TaxBracketRepository taxBracketRepository;
    @Mock private InsuranceConfigRepository insuranceConfigRepository;
    @Mock private ActionLogUtil actionLogUtil;
    @Mock private SecurityUtil securityUtil;
    @Mock private MonthlyPayrollReportPdfGenerator monthlyPayrollReportPdfGenerator;

    @InjectMocks
    private PayslipServiceImpl service;

    @Test
    void exportMonthlyReport_throwsNotFoundWhenPayrollPeriodMissing() {
        UUID payrollPeriodId = UUID.randomUUID();

        when(payrollPeriodRepository.findById(payrollPeriodId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.exportMonthlyReport(payrollPeriodId));
    }

    @Test
    void exportMonthlyReport_throwsWhenPayrollPeriodNotApproved() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setStatus(PayrollPeriod.Status.PENDING);
        period.setIsDeleted(false);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.exportMonthlyReport(period.getId()));

        assertTrue(ex.getMessage().contains("không thể xuất"));
    }

    @Test
    void exportMonthlyReport_throwsWhenNoApprovedPayslipsExist() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setName("Tháng 03/2026");
        period.setStatus(PayrollPeriod.Status.APPROVED);
        period.setIsDeleted(false);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));
        when(payslipRepository
                .findByPayrollPeriodIdAndStatusAndIsDeletedOrderByEmployee_EmployeeCodeAscEmployee_FullNameAsc(
                        period.getId(), Payslip.Status.APPROVED, false))
                .thenReturn(List.of());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.exportMonthlyReport(period.getId()));

        assertEquals("Không có phiếu lương đã duyệt để xuất báo cáo", ex.getMessage());
    }

    @Test
    void exportMonthlyReport_throwsWhenPayrollPeriodSoftDeleted() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setStatus(PayrollPeriod.Status.APPROVED);
        period.setIsDeleted(true);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.exportMonthlyReport(period.getId()));

        assertTrue(ex.getMessage().contains("không thể xuất"));
    }

    @Test
    void exportMonthlyReport_propagatesSanitizedPdfGenerationException() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setName("Tháng 03/2026");
        period.setStatus(PayrollPeriod.Status.APPROVED);
        period.setIsDeleted(false);

        Payslip payslip = new Payslip();
        Employee employee = new Employee();
        employee.setEmployeeCode("NV001");
        employee.setFullName("Nguyễn Văn A");
        payslip.setEmployee(employee);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));
        when(payslipRepository
                .findByPayrollPeriodIdAndStatusAndIsDeletedOrderByEmployee_EmployeeCodeAscEmployee_FullNameAsc(
                        period.getId(), Payslip.Status.APPROVED, false))
                .thenReturn(List.of(payslip));
        when(monthlyPayrollReportPdfGenerator.generate(any(MonthlyPayrollReportData.class)))
                .thenThrow(new PdfGenerationException("Không thể tạo file PDF báo cáo lương", new RuntimeException("font missing")));

        PdfGenerationException ex = assertThrows(
                PdfGenerationException.class,
                () -> service.exportMonthlyReport(period.getId()));

        assertEquals("Không thể tạo file PDF báo cáo lương", ex.getMessage());
    }

    @Test
    void exportMonthlyReport_returnsPdfBytesAndFilenames() {
        PayrollPeriod period = new PayrollPeriod();
        period.setId(UUID.randomUUID());
        period.setName("Tháng 03/2026");
        period.setStatus(PayrollPeriod.Status.APPROVED);
        period.setIsDeleted(false);

        Payslip payslip = new Payslip();
        Employee employee = new Employee();
        employee.setEmployeeCode("NV001");
        employee.setFullName("Nguyễn Văn A");
        payslip.setEmployee(employee);
        payslip.setStatus(Payslip.Status.APPROVED);

        when(payrollPeriodRepository.findById(period.getId())).thenReturn(Optional.of(period));
        when(payslipRepository
                .findByPayrollPeriodIdAndStatusAndIsDeletedOrderByEmployee_EmployeeCodeAscEmployee_FullNameAsc(
                        period.getId(), Payslip.Status.APPROVED, false))
                .thenReturn(List.of(payslip));
        when(securityUtil.getCurrentUsername()).thenReturn("hr.manager");
        when(monthlyPayrollReportPdfGenerator.generate(any(MonthlyPayrollReportData.class)))
                .thenReturn("%PDF-demo".getBytes(StandardCharsets.UTF_8));

        MonthlyPayrollReportExport result = service.exportMonthlyReport(period.getId());

        assertArrayEquals("%PDF-demo".getBytes(StandardCharsets.UTF_8), result.content());
        assertEquals("bao-cao-luong-thang-thang-03-2026.pdf", result.asciiFilename());
        assertTrue(result.utf8Filename().contains("Tháng 03/2026"));
    }
}
```

- [ ] **Step 2: Run the service test to verify it fails**

Run: `.\mvnw.cmd -Dtest=PayslipServiceImplTest test`

Expected: FAIL with compile errors because `PdfGenerationException`, `MonthlyPayrollReportData`, `MonthlyPayrollReportPdfGenerator`, `MonthlyPayrollReportExport`, the export repository method, and `exportMonthlyReport(UUID)` do not exist yet.

- [ ] **Step 3: Add the export result type and repository method**

```java
public class PdfGenerationException extends RuntimeException {
    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

```java
public record MonthlyPayrollReportData(
        PayrollPeriod payrollPeriod,
        List<Payslip> payslips,
        String exportedBy,
        LocalDateTime exportedAt
) {
}
```

```java
@Component
public class MonthlyPayrollReportPdfGenerator {
    MonthlyPayrollReportPdfGenerator() {
    }

    MonthlyPayrollReportPdfGenerator(String fontClasspathLocation) {
    }

    public byte[] generate(MonthlyPayrollReportData reportData) {
        throw new UnsupportedOperationException("Implement in Task 2");
    }
}
```

```java
public record MonthlyPayrollReportExport(
        byte[] content,
        String asciiFilename,
        String utf8Filename
) {
}
```

```java
List<Payslip> findByPayrollPeriodIdAndStatusAndIsDeletedOrderByEmployee_EmployeeCodeAscEmployee_FullNameAsc(
        UUID payrollPeriodId,
        Payslip.Status status,
        boolean isDeleted
);
```

```java
MonthlyPayrollReportExport exportMonthlyReport(UUID payrollPeriodId);
```

- [ ] **Step 4: Implement the minimal service validation and orchestration**

```java
private final MonthlyPayrollReportPdfGenerator monthlyPayrollReportPdfGenerator;

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
        SecurityUtil securityUtil,
        MonthlyPayrollReportPdfGenerator monthlyPayrollReportPdfGenerator
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
    this.monthlyPayrollReportPdfGenerator = monthlyPayrollReportPdfGenerator;
}

@Override
@Transactional(readOnly = true)
public MonthlyPayrollReportExport exportMonthlyReport(UUID payrollPeriodId) {
    PayrollPeriod period = payrollPeriodRepository.findById(payrollPeriodId)
            .orElseThrow(() -> new ResourceNotFoundException(PayrollPeriod.class, payrollPeriodId));

    if (period.getStatus() != PayrollPeriod.Status.APPROVED || Boolean.TRUE.equals(period.getIsDeleted())) {
        throw new IllegalStateException("Kỳ lương này không thể xuất báo cáo");
    }

    List<Payslip> payslips = payslipRepository
            .findByPayrollPeriodIdAndStatusAndIsDeletedOrderByEmployee_EmployeeCodeAscEmployee_FullNameAsc(
                    payrollPeriodId, Payslip.Status.APPROVED, false);

    if (payslips.isEmpty()) {
        throw new IllegalStateException("Không có phiếu lương đã duyệt để xuất báo cáo");
    }

    MonthlyPayrollReportData reportData = new MonthlyPayrollReportData(
            period,
            payslips,
            securityUtil.getCurrentUsername(),
            LocalDateTime.now());

    byte[] content = monthlyPayrollReportPdfGenerator.generate(reportData);
    return new MonthlyPayrollReportExport(
            content,
            buildAsciiFilename(period.getName()),
            buildUtf8Filename(period.getName()));
}

private String buildAsciiFilename(String payrollPeriodName) {
    String rawName = payrollPeriodName == null ? "khong-ten" : payrollPeriodName;
    String normalized = Normalizer.normalize(rawName, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .replace('đ', 'd')
            .replace('Đ', 'D');
    String slug = normalized
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("(^-|-$)", "");
    return "bao-cao-luong-thang-" + (slug.isBlank() ? "khong-ten" : slug) + ".pdf";
}

private String buildUtf8Filename(String payrollPeriodName) {
    String safeName = (payrollPeriodName == null || payrollPeriodName.isBlank()) ? "ky-luong" : payrollPeriodName;
    return "bao-cao-luong-thang-" + safeName + ".pdf";
}
```

- [ ] **Step 5: Run the service test to verify it passes**

Run: `.\mvnw.cmd -Dtest=PayslipServiceImplTest test`

Expected: PASS with 6 passing tests.

- [ ] **Step 6: Commit the contract and service validation**

```bash
git add src/main/java/com/hrm/hrm_api/common/exception/PdfGenerationException.java src/main/java/com/hrm/hrm_api/modules/repository/PayslipRepository.java src/main/java/com/hrm/hrm_api/modules/service/payslip/PayslipService.java src/main/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImpl.java src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportData.java src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGenerator.java src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportExport.java src/test/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImplTest.java
git commit -m "feat: add monthly payroll export service contract"
```

### Task 2: Add Sanitized PDF Generation with a Bundled Unicode Font

**Files:**
- Modify: `src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGenerator.java`
- Create: `src/test/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGeneratorTest.java`
- Create: `src/main/resources/fonts/NotoSans-Regular.ttf`
- Modify: `src/main/java/com/hrm/hrm_api/common/exception/GlobalExceptionHandler.java`
- Test: `src/test/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGeneratorTest.java`

- [ ] **Step 1: Write the failing PDF generator tests**

```java
class MonthlyPayrollReportPdfGeneratorTest {

    @Test
    void generate_returnsPdfDocumentWithVietnameseText() {
        MonthlyPayrollReportPdfGenerator generator =
                new MonthlyPayrollReportPdfGenerator("fonts/NotoSans-Regular.ttf");

        PayrollPeriod period = new PayrollPeriod();
        period.setName("Tháng 03/2026");
        period.setStartDate(LocalDate.of(2026, 3, 1));
        period.setEndDate(LocalDate.of(2026, 3, 31));

        Employee employee = new Employee();
        employee.setEmployeeCode("NV001");
        employee.setFullName("Nguyễn Văn A");

        Payslip payslip = new Payslip();
        payslip.setEmployee(employee);
        payslip.setGrossSalary(new BigDecimal("12000000"));
        payslip.setTaxableIncome(new BigDecimal("13500000"));
        payslip.setTotalDeductions(new BigDecimal("2500000"));
        payslip.setTaxAmount(new BigDecimal("350000"));
        payslip.setNetSalary(new BigDecimal("10650000"));

        MonthlyPayrollReportData reportData = new MonthlyPayrollReportData(
                period,
                List.of(payslip),
                "hr.manager",
                LocalDateTime.of(2026, 3, 31, 17, 30));

        byte[] pdf = generator.generate(reportData);

        assertTrue(pdf.length > 0);
        assertEquals("%PDF-", new String(Arrays.copyOfRange(pdf, 0, 5), StandardCharsets.US_ASCII));
    }

    @Test
    void generate_throwsSanitizedExceptionWhenFontIsMissing() {
        MonthlyPayrollReportPdfGenerator generator =
                new MonthlyPayrollReportPdfGenerator("fonts/missing-font.ttf");

        PdfGenerationException ex = assertThrows(
                PdfGenerationException.class,
                () -> generator.generate(new MonthlyPayrollReportData(
                        new PayrollPeriod(),
                        List.of(),
                        "hr.manager",
                        LocalDateTime.now())));

        assertEquals("Không thể tạo file PDF báo cáo lương", ex.getMessage());
    }
}
```

- [ ] **Step 2: Run the PDF generator test to verify it fails**

Run: `.\mvnw.cmd -Dtest=MonthlyPayrollReportPdfGeneratorTest test`

Expected: FAIL because the generator only contains a placeholder body and the bundled font does not exist yet.

- [ ] **Step 3: Wire the custom exception into the global handler**

```java
@ExceptionHandler(PdfGenerationException.class)
public ResponseEntity<BaseResponse<Object>> handlePdfGeneration(PdfGenerationException ex) {
    return new ResponseEntity<>(
            BaseResponse.error(ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR);
}
```

- [ ] **Step 4: Add the font asset and PDF generator**

Place a vetted Unicode font file at `src/main/resources/fonts/NotoSans-Regular.ttf`.

```java
@Component
public class MonthlyPayrollReportPdfGenerator {

    private final String fontClasspathLocation;

    public MonthlyPayrollReportPdfGenerator() {
        this("fonts/NotoSans-Regular.ttf");
    }

    MonthlyPayrollReportPdfGenerator(String fontClasspathLocation) {
        this.fontClasspathLocation = fontClasspathLocation;
    }

    public byte[] generate(MonthlyPayrollReportData reportData) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ClassPathResource fontResource = new ClassPathResource(fontClasspathLocation);
            PdfFont font = PdfFontFactory.createFont(
                    StreamUtil.inputStreamToArray(fontResource.getInputStream()),
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A3.rotate());
            document.setFont(font);

            document.add(new Paragraph("Báo cáo lương tháng"));
            document.add(new Paragraph("Kỳ lương: " + defaultText(reportData.payrollPeriod().getName())));
            document.add(new Paragraph("Từ ngày: " + reportData.payrollPeriod().getStartDate()));
            document.add(new Paragraph("Đến ngày: " + reportData.payrollPeriod().getEndDate()));
            document.add(new Paragraph("Người xuất: " + defaultText(reportData.exportedBy())));
            document.add(new Paragraph("Thời điểm xuất: " + reportData.exportedAt()));

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

    private void addSummarySection(Document document, List<Payslip> payslips) {
        Table summary = new Table(new float[]{3, 5, 3, 3, 3, 3, 3, 3, 3});
        summary.addHeaderCell("Mã NV");
        summary.addHeaderCell("Họ tên");
        summary.addHeaderCell("Công thực tế");
        summary.addHeaderCell("Tổng công");
        summary.addHeaderCell("Lương gross");
        summary.addHeaderCell("Thu nhập chịu thuế");
        summary.addHeaderCell("Tổng giảm trừ");
        summary.addHeaderCell("Thuế TNCN");
        summary.addHeaderCell("Thực nhận");
        // Fill rows and total row here.
        document.add(summary);
    }

    private void addIncomeSection(Document document, List<Payslip> payslips) {
        // Add all Income columns from the approved spec and a totals row.
    }

    private void addInsuranceSection(Document document, List<Payslip> payslips) {
        // Add all Insurance columns from the approved spec and a totals row.
    }

    private void addTaxSection(Document document, List<Payslip> payslips) {
        // Add all Tax columns from the approved spec and a totals row.
    }

    private void addNetSection(Document document, List<Payslip> payslips) {
        // Add all Net columns from the approved spec and a totals row.
    }
}
```

- [ ] **Step 5: Run the generator and service tests**

Run: `.\mvnw.cmd -Dtest=MonthlyPayrollReportPdfGeneratorTest,PayslipServiceImplTest test`

Expected: PASS with all service and PDF generator tests green.

- [ ] **Step 6: Commit the PDF generation core**

```bash
git add src/main/java/com/hrm/hrm_api/common/exception/PdfGenerationException.java src/main/java/com/hrm/hrm_api/common/exception/GlobalExceptionHandler.java src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportData.java src/main/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGenerator.java src/main/resources/fonts/NotoSans-Regular.ttf src/test/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGeneratorTest.java src/test/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImplTest.java
git commit -m "feat: generate monthly payroll report pdf"
```

## Chunk 2: Delivery Endpoint

### Task 3: Expose the Export Endpoint with Permission and Download Headers

**Files:**
- Create: `src/test/java/com/hrm/hrm_api/modules/controller/PayslipControllerExportTest.java`
- Modify: `src/main/java/com/hrm/hrm_api/modules/controller/PayslipController.java`
- Test: `src/test/java/com/hrm/hrm_api/modules/controller/PayslipControllerExportTest.java`

- [ ] **Step 1: Write the failing controller tests**

```java
@SpringBootTest(
        properties = {
                "spring.flyway.enabled=false",
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
class PayslipControllerExportTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PayslipService payslipService;

    @Test
    void exportMonthlyReport_returnsPdfWhenUserHasExportPermission() throws Exception {
        UUID payrollPeriodId = UUID.randomUUID();
        MonthlyPayrollReportExport export = new MonthlyPayrollReportExport(
                "%PDF-demo".getBytes(StandardCharsets.UTF_8),
                "bao-cao-luong-thang-thang-03-2026.pdf",
                "bao-cao-luong-thang-Tháng 03/2026.pdf");

        when(payslipService.exportMonthlyReport(payrollPeriodId)).thenReturn(export);
        authenticateAs("PAYROLL_EXPORT");

        mockMvc.perform(get("/api/payslips/export/monthly-report/{payrollPeriodId}", payrollPeriodId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, allOf(
                        containsString("attachment;"),
                        containsString("filename=\"bao-cao-luong-thang-thang-03-2026.pdf\""),
                        containsString("filename*=UTF-8''"))));
    }

    @Test
    void exportMonthlyReport_returnsForbiddenWhenPermissionMissing() throws Exception {
        UUID payrollPeriodId = UUID.randomUUID();
        authenticateAs("PAYROLL_READ");

        mockMvc.perform(get("/api/payslips/export/monthly-report/{payrollPeriodId}", payrollPeriodId))
                .andExpect(status().isForbidden());
    }

    @Test
    void exportMonthlyReport_returnsSanitizedServerErrorWhenPdfGenerationFails() throws Exception {
        UUID payrollPeriodId = UUID.randomUUID();
        authenticateAs("PAYROLL_EXPORT");
        when(payslipService.exportMonthlyReport(payrollPeriodId))
                .thenThrow(new PdfGenerationException("Không thể tạo file PDF báo cáo lương", new RuntimeException("font missing")));

        mockMvc.perform(get("/api/payslips/export/monthly-report/{payrollPeriodId}", payrollPeriodId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Không thể tạo file PDF báo cáo lương"));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String authority) {
        UserDetailsImpl principal = new UserDetailsImpl(
                UUID.randomUUID(),
                "hr.manager",
                "hr.manager",
                "secret",
                List.of(new SimpleGrantedAuthority(authority)),
                false);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "secret",
                principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
```

- [ ] **Step 2: Run the controller test to verify it fails**

Run: `.\mvnw.cmd -Dtest=PayslipControllerExportTest test`

Expected: FAIL because the export endpoint does not exist yet.

- [ ] **Step 3: Add the controller endpoint**

```java
@GetMapping("/export/monthly-report/{payrollPeriodId}")
@RequirePermission(module = "PAYROLL", action = PermissionDefinition.EXPORT)
public ResponseEntity<byte[]> exportMonthlyReport(@PathVariable UUID payrollPeriodId) {
    MonthlyPayrollReportExport export = service.exportMonthlyReport(payrollPeriodId);

    String disposition = "attachment; filename=\"" + export.asciiFilename() + "\"; filename*=UTF-8''"
            + UriUtils.encode(export.utf8Filename(), StandardCharsets.UTF_8);

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
            .body(export.content());
}
```

- [ ] **Step 4: Run the controller test to verify it passes**

Run: `.\mvnw.cmd -Dtest=PayslipControllerExportTest test`

Expected: PASS with success, forbidden, and sanitized-500 tests green.

- [ ] **Step 5: Commit the endpoint delivery layer**

```bash
git add src/main/java/com/hrm/hrm_api/modules/controller/PayslipController.java src/test/java/com/hrm/hrm_api/modules/controller/PayslipControllerExportTest.java
git commit -m "feat: add monthly payroll export endpoint"
```

### Task 4: Run Focused Verification and Record the Outcome

**Files:**
- Modify: `docs/superpowers/plans/2026-03-30-monthly-payroll-pdf-report.md`
- Test: `src/test/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImplTest.java`
- Test: `src/test/java/com/hrm/hrm_api/modules/service/payslip/MonthlyPayrollReportPdfGeneratorTest.java`
- Test: `src/test/java/com/hrm/hrm_api/modules/controller/PayslipControllerExportTest.java`

- [ ] **Step 1: Run the focused verification suite**

Run: `.\mvnw.cmd -Dtest=PayslipServiceImplTest,MonthlyPayrollReportPdfGeneratorTest,PayslipControllerExportTest test`

Expected: PASS with all export-related tests green.

- [ ] **Step 2: Run a package-level sanity build**

Run: `.\mvnw.cmd -DskipTests package`

Expected: BUILD SUCCESS and the generated artifact includes `fonts/NotoSans-Regular.ttf`.

- [ ] **Step 3: Verify the packaged artifact contains the bundled font**

Run: `jar tf (Get-ChildItem target -Filter *.jar | Select-Object -First 1).FullName | Select-String 'fonts/NotoSans-Regular.ttf'`

Expected: one match for `BOOT-INF/classes/fonts/NotoSans-Regular.ttf`.

- [ ] **Step 4: Update this plan with any implementation notes that mattered**

```markdown
- Added `PdfGenerationException` because the global exception handler exposes root causes by default.
- Bundled `NotoSans-Regular.ttf` under `src/main/resources/fonts/` to keep Unicode rendering portable.
- Returned `MonthlyPayrollReportExport` so controller filename logic stays deterministic and testable.
```

- [ ] **Step 5: Commit the final verification state**

```bash
git add docs/superpowers/plans/2026-03-30-monthly-payroll-pdf-report.md
git commit -m "chore: record monthly payroll export verification"
```

## Implementation Notes

- Added `PdfGenerationException` because the global exception handler would otherwise expose low-level renderer details on PDF failures.
- Bundled `NotoSans-Regular.ttf` under `src/main/resources/fonts/` and verified it is packaged into `BOOT-INF/classes/fonts/`.
- Returned `MonthlyPayrollReportExport` so the controller can build deterministic ASCII and UTF-8 download filenames without re-querying payroll period data.
- Seeded `SecurityContextHolder` directly in controller tests because permission enforcement happens in `PermissionAspect`, not only in the servlet security filter chain.
