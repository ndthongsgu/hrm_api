# Monthly Payroll PDF Report Design

## Summary

Add a monthly payroll report export feature that generates a PDF for HR from approved payslips in a selected payroll period. The first iteration targets a single report type: a monthly summary PDF for HR, not employee-specific payslip PDFs.

The feature must:

- Export one PDF per payroll period
- Include all relevant fields already stored on `Payslip`
- Only include payslips with `status = APPROVED` and `isDeleted = false`
- Follow the existing `controller -> service -> repository` structure
- Reuse the iText PDF dependency already present in the project

## Goals

- Let HR export a stable monthly payroll report from finalized payroll data
- Keep the export feature aligned with the existing payroll module patterns
- Preserve room for a second-phase feature: individual employee payslip PDFs

## Non-Goals

- Generating individual employee payslip PDFs in this iteration
- Exporting pending or soft-deleted payslips
- Adding Excel export in this iteration
- Changing payroll calculation rules or `Payslip` persistence structure

## Current Context

The codebase already has the core payroll workflow:

- `PayrollPeriod` defines the monthly payroll window
- `AttendanceSummary`, `SalaryAdjustment`, and contract data are used to generate `Payslip`
- `PayslipController` exposes search, detail, generation, approval, activation, and deactivation flows
- `PayslipServiceImpl` already centralizes payroll-related business logic

There is no export/report endpoint yet, but the project already includes iText dependencies for PDF generation. That makes PDF export an additive feature rather than a platform change.

## User Flow

1. HR selects a payroll period that has finalized payslips.
2. HR calls the monthly export endpoint.
3. The backend validates the payroll period and loads all approved, non-deleted payslips for that period.
4. The backend generates a grouped PDF report.
5. The API returns the PDF as a downloadable file.

## Proposed API

### Endpoint

`GET /api/payslips/export/monthly-report/{payrollPeriodId}`

### Authorization

- Module: `PAYROLL`
- Permission: `EXPORT`

### Response

- Success: `200 OK`
- Content-Type: `application/pdf`
- `Content-Disposition: attachment; filename="bao-cao-luong-thang-<payroll-period-name>.pdf"`

### Failure Cases

- `404 Not Found` when the payroll period does not exist
- `400 Bad Request` when the payroll period exists but has no approved payslips to export
- `500 Internal Server Error` when PDF generation fails unexpectedly

## Architecture

### Controller

Add a new export endpoint to `PayslipController`.

Responsibilities:

- Receive `payrollPeriodId`
- Enforce `@RequirePermission(module = "PAYROLL", action = PermissionDefinition.EXPORT)`
- Call service export logic
- Return raw PDF bytes with download headers

### Service

Extend `PayslipService` with a dedicated export method, for example:

`byte[] exportMonthlyReport(UUID payrollPeriodId);`

Add the implementation to `PayslipServiceImpl`.

Responsibilities:

- Validate payroll period existence
- Load approved, non-deleted payslips for the payroll period
- Reject empty result sets with a business error
- Delegate PDF rendering to a dedicated generator class

### PDF Generator

Create a dedicated PDF rendering class, for example:

- `MonthlyPayrollReportPdfGenerator`

Responsibilities:

- Build the document layout
- Render header metadata
- Render grouped tables by payroll domain
- Format numbers safely and consistently
- Return the generated document as bytes

This keeps formatting code out of `PayslipServiceImpl`, which already contains dense payroll business logic.

## Data Retrieval

The export should read from persisted `Payslip` records rather than recalculating payroll.

Filtering rules:

- `payrollPeriod.id = :payrollPeriodId`
- `status = APPROVED`
- `isDeleted = false`

This ensures the report represents finalized payroll data and stays consistent with approval workflows already present in the module.

Repository impact:

- Prefer adding a repository method dedicated to approved export retrieval rather than reusing looser search behavior
- Example direction: `findByPayrollPeriodIdAndStatusAndIsDeleted(...)`

## Report Structure

Because the user wants the report to include the full payslip dataset, a single wide table would make the PDF difficult to read. The report should therefore be split into multiple grouped sections.

### Document Layout

- Page size: `A3 landscape`
- Header on the first page
- Repeated section titles for grouped tables
- Consistent numeric alignment for currency and totals

### Header Section

Include:

- Company/system title
- Report title: monthly payroll report
- Payroll period name
- Payroll period start date and end date
- Export timestamp
- Exported by user
- Number of employees included

### Section 1: Summary

This section gives HR the fast overview.

Columns:

- Employee code
- Employee name
- Workdays
- Total workdays
- Gross salary
- Taxable income
- Total deductions
- Tax amount
- Net salary
- Payslip status

### Section 2: Income

Columns:

- Employee code
- Employee name
- Gross salary
- Salary by workdays
- Taxable and insurable allowance
- Taxable and non-insurable allowance
- Non-taxable and insurable allowance
- Non-taxable and non-insurable allowance
- Total bonus
- Total bonus non-tax

### Section 3: Insurance

Columns:

- Employee code
- Employee name
- BHXH salary base
- BHXH amount
- BHYT amount
- BHTN amount

### Section 4: Tax

Columns:

- Employee code
- Employee name
- Taxable income
- Deduction personal
- Deduction dependents
- Tax fixed deduction
- Total deductions
- Assessable income
- Tax amount

### Section 5: Net

Columns:

- Employee code
- Employee name
- Net salary

### Totals

At the bottom of each relevant section, include totals for additive money fields. Do not total identity columns or rate-like fields that are not meaningful in aggregate.

## Formatting Rules

- Monetary values should use a consistent decimal format
- Null values should render as zero or blank based on semantic meaning
- Dates should use one consistent display format across the document
- Table headers should remain readable in print
- Long content should wrap or truncate safely without breaking the table layout

## Error Handling

### Payroll Period Not Found

If `payrollPeriodId` does not exist, throw the existing not-found flow used elsewhere in the module.

### No Approved Payslips

If the period exists but no approved payslips are available, return a business error with a clear message such as:

`Không có phiếu lương đã duyệt để xuất báo cáo`

### PDF Rendering Failure

Wrap unexpected PDF generation exceptions in a controlled server-side failure path. The API should not leak low-level rendering details in the response body.

## Security

The export endpoint must require the same payroll module authorization model already used in the controller layer.

This export should not bypass approval state:

- Pending payslips are excluded
- Soft-deleted payslips are excluded

That keeps the report aligned with the final payroll record visible to HR.

## Testing Strategy

### Service Tests

Add coverage for:

- Payroll period not found
- Payroll period exists but no approved payslips
- Approved payslips exist and the method returns a non-empty PDF byte array

### Controller Tests

Add coverage for:

- Authorized export returns `200`
- Response content type is `application/pdf`
- Response contains `Content-Disposition` attachment header
- Permission guard blocks unauthorized access

### PDF Assertions

The first iteration does not need visual snapshot testing. Basic structural verification is enough:

- Byte array is non-empty
- PDF header signature is valid
- Known report title text may optionally be asserted if extraction is straightforward

## Design Trade-Offs

### Why PDF and not Excel first

The user explicitly prioritized PDF. The project already includes PDF dependencies, so PDF has lower integration cost than introducing a spreadsheet pipeline.

### Why grouped sections instead of one full-width table

The `Payslip` entity contains too many fields for a readable single-table PDF. Grouped sections preserve the full dataset while staying usable for HR and printing.

### Why read from `Payslip` instead of recalculating

Export should reflect the approved payroll state, not recompute values from upstream inputs that may have changed after payslip generation.

### Why use a dedicated PDF generator class

`PayslipServiceImpl` already has substantial business logic. Mixing PDF layout concerns into that class would reduce maintainability and make testing harder.

## Implementation Outline

1. Add repository support for fetching approved payslips by payroll period.
2. Extend `PayslipService` and `PayslipServiceImpl` with monthly report export logic.
3. Add a PDF generator class for the document layout.
4. Add the new controller endpoint with export permission.
5. Add focused tests for service and controller behavior.

## Future Extension

Once this monthly HR report is stable, the next export feature can build on the same PDF utilities to support:

- Individual employee payslip PDFs
- Combined archive export per payroll period
- Optional Excel export for downstream accounting workflows
