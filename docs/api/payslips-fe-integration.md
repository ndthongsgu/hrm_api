# Payslips API Guide For Frontend

## Mục tiêu

Tài liệu này mô tả **backend hiện đang có gì** ở module `payslips` để FE có thể:

- xây UI quản lý phiếu lương
- gọi đúng API
- hiểu response shape
- xử lý download PDF đúng cách
- tránh các bẫy tích hợp do behavior hiện tại của backend

Phạm vi tài liệu là module:

- `GET/POST/PUT/DELETE /api/payslips...`
- export báo cáo lương tháng PDF

## Tổng quan nhanh

Backend hiện hỗ trợ các use case chính:

1. Tìm kiếm danh sách phiếu lương có phân trang
2. Lấy chi tiết một phiếu lương
3. Tạo phiếu lương cho một nhân viên trong một kỳ lương
4. Cập nhật phiếu lương
5. Soft delete / activate phiếu lương
6. Duyệt phiếu lương
7. Export PDF báo cáo lương tháng theo `payrollPeriodId`
8. Lấy phiếu lương đã duyệt của một nhân viên trong một kỳ lương

## Auth và quyền

Tất cả endpoint dưới đây đều cần JWT đăng nhập hợp lệ, vì security config đang yêu cầu authenticate cho mọi route trừ `/api/auth/**`.

### Permission codes FE nên map

- `PAYROLL_READ`
- `PAYROLL_CREATE`
- `PAYROLL_UPDATE`
- `PAYROLL_DEACTIVATE`
- `PAYROLL_ACTIVATE`
- `PAYROLL_APPROVE`
- `PAYROLL_EXPORT`

### Gợi ý FE

- dùng permission để ẩn/hiện button
- không chỉ dựa vào UI; backend vẫn sẽ chặn nếu thiếu quyền
- nếu mở trực tiếp URL API trên browser address bar (ví dụ `/api/payslips/export/monthly-report/{id}`), request sẽ không có `Authorization` header nên nhận `401 Unauthorized`

## Response conventions

### 1. JSON success wrapper

Phần lớn endpoint JSON dùng wrapper:

```json
{
  "success": true,
  "message": null,
  "data": {},
  "timestamp": "2026-03-31T00:00:00Z"
}
```

### 2. JSON error wrapper

Nhiều lỗi nghiệp vụ trả:

```json
{
  "success": false,
  "message": "Thao tác không hợp lệ: Không có phiếu lương đã duyệt để xuất báo cáo",
  "data": null,
  "timestamp": "2026-03-31T00:00:00Z"
}
```

### 3. Một số lỗi validation / not found dùng shape khác

Ví dụ `404` hoặc validation có thể trả `ErrorResponse`:

```json
{
  "error": "Không tìm thấy",
  "message": "Payslip not found with id: ...",
  "timestamp": "2026-03-31T00:00:00Z"
}
```

Vì vậy FE nên:

- ưu tiên đọc `message`
- fallback sang `error` nếu không có `message`

### 4. Export PDF không dùng JSON khi thành công

`GET /api/payslips/export/monthly-report/{payrollPeriodId}` khi thành công trả:

- `Content-Type: application/pdf`
- body là binary PDF

Khi lỗi, cùng endpoint này lại trả JSON error như các API khác.

FE phải xử lý endpoint này theo kiểu:

- success: `blob/arrayBuffer`
- error: parse JSON từ response body nếu status không phải `2xx`

## Pagination format

API search dùng Spring Pageable.

### Query params FE có thể dùng

- `page`
- `size`
- `sort`

Ví dụ:

`POST /api/payslips?page=0&size=20&sort=createdAt,desc`

### Response phân trang

```json
{
  "success": true,
  "data": {
    "items": [],
    "totalElements": 120,
    "totalPages": 6,
    "page": 0,
    "size": 20
  }
}
```

## Endpoints chi tiết

### 1. Search payslips

- Method: `POST`
- URL: `/api/payslips`
- Permission: `PAYROLL_READ`
- Query params: `page`, `size`, `sort`
- Body: optional JSON filter

#### Supported filters hiện tại

```json
{
  "employeeName": "An",
  "payrollPeriodId": "uuid",
  "status": "APPROVED"
}
```

#### FE notes

- endpoint này là `POST`, không phải `GET`
- filter `payrollPeriodId` và `status` là ổn nhất để dùng
- `status` hiện nhận string enum: `APPROVED` hoặc `PENDING`

#### Sample request

```http
POST /api/payslips?page=0&size=20
Authorization: Bearer <token>
Content-Type: application/json

{
  "payrollPeriodId": "4b8d7c7c-8c7f-4c18-a3d3-1fb3f14b4b5f",
  "status": "APPROVED"
}
```

#### Sample response

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "id": "uuid",
        "createdAt": "2026-03-31T00:00:00",
        "updatedAt": "2026-03-31T00:00:00",
        "createdBy": "hr.manager",
        "updatedBy": "hr.manager",
        "version": 0,
        "isDeleted": false,
        "payrollPeriod": {
          "id": "uuid",
          "name": "Tháng 03/2026",
          "startDate": "2026-03-01",
          "endDate": "2026-03-31",
          "standardWorkingDays": 26.0,
          "status": "APPROVED"
        },
        "employee": {
          "id": "uuid",
          "fullName": "Nguyễn Văn A",
          "employeeCode": "NV001"
        },
        "grossSalary": 12000000.00,
        "netSalary": 10650000.00,
        "status": "APPROVED"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```

### 2. Get payslip detail

- Method: `GET`
- URL: `/api/payslips/{id}`
- Permission: `PAYROLL_READ`

Dùng endpoint này để mở modal/detail drawer trước khi approve, update, hoặc hiển thị breakdown đầy đủ.

### 3. Generate payslip for one employee

- Method: `POST`
- URL: `/api/payslips/generate/{payrollPeriodId}/employee/{employeeId}`
- Permission: `PAYROLL_CREATE`

#### Backend conditions hiện tại

Backend chỉ generate được nếu:

- payroll period là `APPROVED`
- payroll period chưa bị xóa mềm
- attendance summary của nhân viên trong kỳ đã `APPROVED`
- có hợp đồng hiệu lực
- chưa tồn tại payslip chưa xóa cho nhân viên đó trong kỳ

#### FE notes

Nếu backend trả `400`, message nghiệp vụ thường khá rõ. FE nên hiện thẳng message đó.

### 4. Update payslip

- Method: `PUT`
- URL: `/api/payslips/{id}`
- Permission: `PAYROLL_UPDATE`
- Body: **full payslip object**

#### Rất quan trọng cho FE

Backend hiện đang gọi `save(entity)` trực tiếp sau khi set `id`, tức là update này gần kiểu **replace toàn bộ entity** hơn là patch.

FE nên làm theo flow:

1. gọi `GET /api/payslips/{id}`
2. merge field user chỉnh sửa vào object đang có
3. gửi lại object đầy đủ

Không nên gửi partial payload kiểu:

```json
{
  "status": "APPROVED"
}
```

vì có thể làm mất các field khác.

### 5. Deactivate payslip

- Method: `DELETE`
- URL: `/api/payslips/{id}`
- Permission: `PAYROLL_DEACTIVATE`

Đây là soft delete, không xóa hard khỏi DB.

### 6. Activate payslip

- Method: `PUT`
- URL: `/api/payslips/{id}/activate`
- Permission: `PAYROLL_ACTIVATE`

### 7. Approve payslip

- Method: `POST`
- URL: `/api/payslips/{id}/approve`
- Permission: `PAYROLL_APPROVE`

Backend hiện set:

```text
status = APPROVED
```

Không có endpoint reject ở module này.

### 8. Export monthly payroll report PDF

- Method: `GET`
- URL: `/api/payslips/export/monthly-report/{payrollPeriodId}`
- Permission: `PAYROLL_EXPORT`
- Success response: raw PDF

#### Điều kiện export

Backend chỉ export khi:

- payroll period tồn tại
- payroll period có `status = APPROVED`
- payroll period `isDeleted = false`
- có ít nhất một payslip `APPROVED` và `isDeleted = false` trong kỳ đó

#### Error cases FE nên expect

- `404` nếu `payrollPeriodId` không tồn tại
- `400` nếu kỳ lương chưa được duyệt hoặc đã bị soft delete
- `400` nếu kỳ lương đã duyệt nhưng chưa có payslip `APPROVED`

#### Success headers

Ví dụ:

```http
Content-Type: application/pdf
Content-Disposition: attachment; filename="bao-cao-luong-thang-thang-03-2026.pdf"; filename*=UTF-8''bao-cao-luong-thang-Th%C3%A1ng%2003%2F2026.pdf
```

#### FE implementation gợi ý

```ts
const res = await fetch(`/api/payslips/export/monthly-report/${payrollPeriodId}`, {
  method: 'GET',
  headers: {
    Authorization: `Bearer ${token}`
  }
});

if (!res.ok) {
  const error = await res.json().catch(() => null);
  throw new Error(error?.message || error?.error || 'Export failed');
}

const blob = await res.blob();
const url = URL.createObjectURL(blob);
const a = document.createElement('a');
const disposition = res.headers.get('Content-Disposition') || '';
const fallbackName = 'bao-cao-luong-thang.pdf';
const match = disposition.match(/filename="([^"]+)"/);
a.href = url;
a.download = match?.[1] || fallbackName;
a.click();
URL.revokeObjectURL(url);
```

#### FE UX gợi ý

- disable nút export nếu chưa chọn payroll period
- ưu tiên chỉ cho export ở kỳ lương `APPROVED`
- nếu export lỗi vì không có payslip approved, hiện message từ backend
- không dùng thẻ `<a href=".../export/monthly-report/{id}">` để gọi trực tiếp nếu backend dùng Bearer token; hãy dùng `fetch/axios` kèm header `Authorization`

### 9. Get employee payslip by payroll period

- Method: `GET`
- URL: `/api/payslips/employee/{employeeId}/payroll-period/{payrollPeriodId}`
- Permission annotation: không có `@RequirePermission`
- Nhưng thực tế vẫn cần JWT do security global

#### Behavior hiện tại

Service chỉ tìm payslip:

- đúng `employeeId`
- đúng `payrollPeriodId`
- `status = APPROVED`
- `isDeleted = false`

#### FE notes

- `404` nếu `payrollPeriodId` không tồn tại
- `400` nếu kỳ lương chưa được duyệt hoặc đã bị soft delete
- `400` nếu chưa có payslip `APPROVED` cho nhân viên trong kỳ
FE mobile / employee portal nên hiện thẳng `message` từ backend cho các case này.

## Data model FE cần biết

### Payslip base fields

Từ `BaseEntity`:

- `id`
- `createdAt`
- `updatedAt`
- `createdBy`
- `updatedBy`
- `version`
- `isDeleted`

### Quan hệ chính

- `payrollPeriod`
- `employee`
- `contract`

### Nhóm Income

- `grossSalary`
- `salaryByWorkdays`
- `workdays`
- `totalWorkdays`
- `taxableAndInsurableAllowance`
- `taxableAndNonInsurableAllowance`
- `nonTaxableAndInsurableAllowance`
- `nonTaxableAndNonInsurableAllowance`
- `totalBonus`
- `totalBonusNonTax`

### Nhóm Insurance

- `bhxhSalaryBase`
- `bhxhAmount`
- `bhytAmount`
- `bhtnAmount`

### Nhóm Tax

- `taxableIncome`
- `deductionPersonal`
- `deductionDependents`
- `taxFixedDeduction`
- `totalDeductions`
- `assessableIncome`
- `taxRate`
- `taxAmount`

### Nhóm Net

- `netSalary`

### Status enum

- `PENDING`
- `APPROVED`

## FE screen mapping gợi ý

### 1. Payroll Payslips List Screen

Nên có:

- filter theo payroll period
- filter theo status
- table danh sách payslips
- action approve
- action deactivate / activate
- action view detail
- action export PDF theo kỳ lương

### 2. Payslip Detail Drawer / Modal

Nên nhóm field giống backend:

1. Tổng quan
2. Income
3. Insurance
4. Tax
5. Net

Lợi ích:

- đồng nhất với report PDF backend
- user dễ đối chiếu giữa UI và file export

### 3. Employee Self-Service / Mobile

Flow phù hợp:

1. chọn kỳ lương
2. gọi `/employee/{employeeId}/payroll-period/{payrollPeriodId}`
3. nếu `data == null` thì hiện trạng thái “Chưa có phiếu lương được duyệt”

## Các caveat FE cần biết ngay

### 1. Search đang dùng `employeeName`, nhưng backend implementation có rủi ro

Trong service search, backend hiện đang filter theo:

```text
employee.name
```

trong khi entity `Employee` đang dùng field:

```text
fullName
```

Vì vậy:

- FE không nên phụ thuộc mạnh vào filter `employeeName` ở thời điểm này
- nên ưu tiên filter bằng `payrollPeriodId` + `status`
- nếu vẫn dùng search theo tên, cần QA kỹ trong môi trường thực tế

### 2. Update không phải PATCH

Đừng gửi partial payload.

### 3. Export success là binary, export error là JSON

FE phải branch theo `res.ok`.

### 4. Mobile payslip endpoint giờ trả lỗi nghiệp vụ rõ ràng

Không còn nên giả định `data: null` là trạng thái hợp lệ khi chưa có payslip được duyệt.

### 5. Numeric values là `BigDecimal`

FE nên hiển thị như số tiền:

- có separator hàng nghìn
- tối đa 2 chữ số thập phân

### 6. PDF filename có 2 dạng

Backend đang trả:

- ASCII fallback filename
- UTF-8 filename

FE web chỉ cần đọc `filename="..."` là đủ cho download cơ bản.

## Recommended FE flow cho chức năng export tháng

### Flow đề xuất

1. User chọn `PayrollPeriod`
2. FE kiểm tra period đang ở trạng thái `APPROVED`
3. FE cho phép bấm `Export PDF`
4. FE gọi endpoint export với `responseType = blob`
5. Nếu thành công thì tải file
6. Nếu lỗi thì parse JSON và toast message

### Disable conditions nên có

- chưa chọn kỳ lương
- đang export
- user không có `PAYROLL_EXPORT`

## Checklist tích hợp cho FE

- map permission `PAYROLL_*`
- dùng `POST /api/payslips` cho list
- truyền `page` / `size` bằng query param
- không dùng partial payload cho update
- handle `data: null` ở endpoint payslip cá nhân
- handle binary download cho export
- parse JSON error ở export khi status không phải `2xx`
- ưu tiên filter theo `payrollPeriodId` và `status`

## File backend liên quan

Nếu FE cần đối chiếu thêm với code backend, xem:

- `src/main/java/com/hrm/hrm_api/modules/controller/PayslipController.java`
- `src/main/java/com/hrm/hrm_api/modules/service/payslip/PayslipServiceImpl.java`
- `src/main/java/com/hrm/hrm_api/modules/entity/Payslip.java`
- `src/main/java/com/hrm/hrm_api/modules/dto/base/BaseResponse.java`
- `src/main/java/com/hrm/hrm_api/modules/dto/base/PageResponse.java`
