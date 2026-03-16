-- Dữ liệu mẫu cho hệ thống HRM
-- Chạy script này trong DBeaver kết nối đến PostgreSQL

-- Thêm Phòng Ban
INSERT INTO departments (id, name, code, parent_id, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440000'::uuid, 'Nhân Sự', 'HR', NULL, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440001'::uuid, 'Công Nghệ Thông Tin', 'IT', NULL, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002'::uuid, 'Tài Chính', 'FIN', NULL, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440003'::uuid, 'Marketing', 'MKT', NULL, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440004'::uuid, 'Vận Hành', 'OPS', NULL, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440005'::uuid, 'Tuyển Dụng Nhân Sự', 'HR_REC', '550e8400-e29b-41d4-a716-446655440000'::uuid, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440006'::uuid, 'Đào Tạo Nhân Sự', 'HR_TRN', '550e8400-e29b-41d4-a716-446655440000'::uuid, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440007'::uuid, 'Phát Triển IT', 'IT_DEV', '550e8400-e29b-41d4-a716-446655440001'::uuid, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440008'::uuid, 'Hỗ Trợ IT', 'IT_SUP', '550e8400-e29b-41d4-a716-446655440001'::uuid, NOW(), NOW());

-- Thêm Chức Vụ
INSERT INTO positions (id, department_id, name, code, standard_responsibility_allowance, created_at, updated_at) VALUES
('660e8400-e29b-41d4-a716-446655440000'::uuid, '550e8400-e29b-41d4-a716-446655440000'::uuid, 'Quản Lý Nhân Sự', 'HR_MGR', 5000000.00, NOW(), NOW()),
('660e8400-e29b-41d4-a716-446655440001'::uuid, '550e8400-e29b-41d4-a716-446655440000'::uuid, 'Chuyên Viên Nhân Sự', 'HR_SPEC', 3000000.00, NOW(), NOW()),
('660e8400-e29b-41d4-a716-446655440002'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid, 'Quản Lý IT', 'IT_MGR', 6000000.00, NOW(), NOW()),
('660e8400-e29b-41d4-a716-446655440003'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid, 'Lập Trình Viên', 'SW_DEV', 4000000.00, NOW(), NOW()),
('660e8400-e29b-41d4-a716-446655440004'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid, 'Quản Trị Hệ Thống', 'SYS_ADMIN', 3500000.00, NOW(), NOW()),
('660e8400-e29b-41d4-a716-446655440005'::uuid, '550e8400-e29b-41d4-a716-446655440002'::uuid, 'Quản Lý Tài Chính', 'FIN_MGR', 5500000.00, NOW(), NOW()),
('660e8400-e29b-41d4-a716-446655440006'::uuid, '550e8400-e29b-41d4-a716-446655440002'::uuid, 'Kế Toán', 'ACC', 3200000.00, NOW(), NOW()),
('660e8400-e29b-41d4-a716-446655440007'::uuid, '550e8400-e29b-41d4-a716-446655440003'::uuid, 'Quản Lý Marketing', 'MKT_MGR', 4800000.00, NOW(), NOW()),
('660e8400-e29b-41d4-a716-446655440008'::uuid, '550e8400-e29b-41d4-a716-446655440003'::uuid, 'Chuyên Viên Marketing', 'MKT_SPEC', 2800000.00, NOW(), NOW()),
('660e8400-e29b-41d4-a716-446655440009'::uuid, '550e8400-e29b-41d4-a716-446655440004'::uuid, 'Quản Lý Vận Hành', 'OPS_MGR', 5200000.00, NOW(), NOW());

-- Thêm Vai Trò
INSERT INTO roles (id, name, description, permissions_json, created_at, updated_at) VALUES
('770e8400-e29b-41d4-a716-446655440000'::uuid, 'Quản Trị Viên', 'Quản trị viên hệ thống với quyền truy cập đầy đủ', '{"EMPLOYEE": ["READ","CREATE","UPDATE","DELETE"], "DEPARTMENT": ["READ","CREATE","UPDATE","DELETE"], "POSITION": ["READ","CREATE","UPDATE","DELETE"], "PAYROLL": ["READ","CREATE","UPDATE","DELETE"], "USER": ["READ","CREATE","UPDATE","DELETE"]}', NOW(), NOW()),
('770e8400-e29b-41d4-a716-446655440001'::uuid, 'Quản Lý Nhân Sự', 'Quản lý nhân sự', '{"EMPLOYEE": ["READ","CREATE","UPDATE"], "DEPARTMENT": ["READ"], "POSITION": ["READ"], "PAYROLL": ["READ","CREATE","UPDATE"]}', NOW(), NOW()),
('770e8400-e29b-41d4-a716-446655440002'::uuid, 'Nhân Viên', 'Nhân viên thường', '{"EMPLOYEE": ["READ"], "PAYROLL": ["READ"]}', NOW(), NOW()),
('770e8400-e29b-41d4-a716-446655440003'::uuid, 'Quản Lý IT', 'Quản lý công nghệ thông tin', '{"EMPLOYEE": ["READ"], "DEPARTMENT": ["READ"], "POSITION": ["READ","CREATE","UPDATE"]}', NOW(), NOW());

-- Thêm Người Dùng (mật khẩu được mã hóa BCrypt, nhưng dùng demo - trong production dùng mã hóa thực tế)
INSERT INTO users (id, username, password, active, is_admin, employee_id, created_at, updated_at) VALUES
('880e8400-e29b-41d4-a716-446655440001'::uuid, 'ql_nhansu', '$2a$10$example.hash.for.hr', true, false, NULL, NOW(), NOW()), -- mật khẩu: hr123
('880e8400-e29b-41d4-a716-446655440002'::uuid, 'john_doe', '$2a$10$example.hash.for.john', true, false, NULL, NOW(), NOW()), -- mật khẩu: emp123
('880e8400-e29b-41d4-a716-446655440003'::uuid, 'jane_smith', '$2a$10$example.hash.for.jane', true, false, NULL, NOW(), NOW()); -- mật khẩu: emp123

-- Thêm Nhân Viên
INSERT INTO employees (id, full_name, employee_code, email, phone, department_id, position_id, tax_code, bank_account, bank_name, status, created_at, updated_at) VALUES
('990e8400-e29b-41d4-a716-446655440000'::uuid, 'Nguyễn Văn A', 'NV001', 'nguyenvana@company.com', '0123456789', '550e8400-e29b-41d4-a716-446655440001'::uuid, '660e8400-e29b-41d4-a716-446655440003'::uuid, '123456789', '1234567890', 'Vietcombank', 'ACTIVE', NOW(), NOW()),
('990e8400-e29b-41d4-a716-446655440001'::uuid, 'Trần Thị B', 'NV002', 'tranthib@company.com', '0987654321', '550e8400-e29b-41d4-a716-446655440000'::uuid, '660e8400-e29b-41d4-a716-446655440001'::uuid, '987654321', '0987654321', 'Techcombank', 'ACTIVE', NOW(), NOW()),
('990e8400-e29b-41d4-a716-446655440002'::uuid, 'Lê Văn C', 'NV003', 'levanc@company.com', '0111111111', '550e8400-e29b-41d4-a716-446655440002'::uuid, '660e8400-e29b-41d4-a716-446655440006'::uuid, '111111111', '1111111111', 'BIDV', 'ACTIVE', NOW(), NOW()),
('990e8400-e29b-41d4-a716-446655440003'::uuid, 'Phạm Thị D', 'NV004', 'phamthid@company.com', '0222222222', '550e8400-e29b-41d4-a716-446655440003'::uuid, '660e8400-e29b-41d4-a716-446655440008'::uuid, '222222222', '2222222222', 'Agribank', 'ACTIVE', NOW(), NOW()),
('990e8400-e29b-41d4-a716-446655440004'::uuid, 'Hoàng Văn E', 'NV005', 'hoangvane@company.com', '0333333333', '550e8400-e29b-41d4-a716-446655440001'::uuid, '660e8400-e29b-41d4-a716-446655440004'::uuid, '333333333', '3333333333', 'VietinBank', 'ACTIVE', NOW(), NOW());

-- Cập nhật người dùng với employee_id
UPDATE users SET employee_id = '990e8400-e29b-41d4-a716-446655440000'::uuid WHERE id = '880e8400-e29b-41d4-a716-446655440002'::uuid;
UPDATE users SET employee_id = '990e8400-e29b-41d4-a716-446655440001'::uuid WHERE id = '880e8400-e29b-41d4-a716-446655440003'::uuid;

-- Thêm Vai Trò Người Dùng
INSERT INTO user_roles (id, user_id, role_id) VALUES
('aa0e8400-e29b-41d4-a716-446655440001'::uuid, '880e8400-e29b-41d4-a716-446655440001'::uuid, '770e8400-e29b-41d4-a716-446655440001'::uuid), -- ql_nhansu -> Quản Lý Nhân Sự
('aa0e8400-e29b-41d4-a716-446655440002'::uuid, '880e8400-e29b-41d4-a716-446655440002'::uuid, '770e8400-e29b-41d4-a716-446655440002'::uuid), -- john_doe -> Nhân Viên
('aa0e8400-e29b-41d4-a716-446655440003'::uuid, '880e8400-e29b-41d4-a716-446655440003'::uuid, '770e8400-e29b-41d4-a716-446655440002'::uuid); -- jane_smith -> Nhân Viên

-- Thêm Cài Đặt Hệ Thống cho Tính Lương
INSERT INTO sys_settings (id, key_name, value, description, effective_date) VALUES
('bb0e8400-e29b-41d4-a716-446655440000'::uuid, 'LUONG_TOI_THIEU', 4420000.00, 'Lương tối thiểu hàng tháng (VND)', '2024-01-01'),
('bb0e8400-e29b-41d4-a716-446655440001'::uuid, 'TY_LE_BHXH', 8.00, 'Tỷ lệ bảo hiểm xã hội (%)', '2024-01-01'),
('bb0e8400-e29b-41d4-a716-446655440002'::uuid, 'TY_LE_BHYT', 1.50, 'Tỷ lệ bảo hiểm y tế (%)', '2024-01-01'),
('bb0e8400-e29b-41d4-a716-446655440003'::uuid, 'TY_LE_BHTN', 1.00, 'Tỷ lệ bảo hiểm thất nghiệp (%)', '2024-01-01'),
('bb0e8400-e29b-41d4-a716-446655440004'::uuid, 'LUONG_TOI_DA_BHXH', 46800000.00, 'Lương tối đa tính bảo hiểm xã hội (VND)', '2024-01-01'),
('bb0e8400-e29b-41d4-a716-446655440005'::uuid, 'PHU_CAP_NGUOI_PHU_THUOC', 3600000.00, 'Phụ cấp người phụ thuộc hàng tháng (VND)', '2024-01-01'),
('bb0e8400-e29b-41d4-a716-446655440006'::uuid, 'GIO_LAM_VIEC_CHUAN', 8.00, 'Giờ làm việc chuẩn mỗi ngày', '2024-01-01'),
('bb0e8400-e29b-41d4-a716-446655440007'::uuid, 'TY_LE_LAM_THEM_1', 1.50, 'Tỷ lệ làm thêm giờ cho đêm/cuối tuần (1.5x)', '2024-01-01'),
('bb0e8400-e29b-41d4-a716-446655440008'::uuid, 'TY_LE_LAM_THEM_2', 2.00, 'Tỷ lệ làm thêm giờ cho ngày lễ (2x)', '2024-01-01');

-- Thêm Bậc Thuế (Thuế thu nhập cá nhân Việt Nam)
INSERT INTO tax_brackets (id, policy_name, min_income, max_income, tax_rate, subtract_amount, is_active, effective_date) VALUES
('cc0e8400-e29b-41d4-a716-446655440000'::uuid, 'Thuế TNCN Việt Nam 2024', 0.00, 5000000.00, 5.00, 0.00, true, '2024-01-01'),
('cc0e8400-e29b-41d4-a716-446655440001'::uuid, 'Thuế TNCN Việt Nam 2024', 5000000.00, 10000000.00, 10.00, 250000.00, true, '2024-01-01'),
('cc0e8400-e29b-41d4-a716-446655440002'::uuid, 'Thuế TNCN Việt Nam 2024', 10000000.00, 18000000.00, 15.00, 750000.00, true, '2024-01-01'),
('cc0e8400-e29b-41d4-a716-446655440003'::uuid, 'Thuế TNCN Việt Nam 2024', 18000000.00, 32000000.00, 20.00, 1650000.00, true, '2024-01-01'),
('cc0e8400-e29b-41d4-a716-446655440004'::uuid, 'Thuế TNCN Việt Nam 2024', 32000000.00, 52000000.00, 25.00, 3250000.00, true, '2024-01-01'),
('cc0e8400-e29b-41d4-a716-446655440005'::uuid, 'Thuế TNCN Việt Nam 2024', 52000000.00, 80000000.00, 30.00, 5850000.00, true, '2024-01-01'),
('cc0e8400-e29b-41d4-a716-446655440006'::uuid, 'Thuế TNCN Việt Nam 2024', 80000000.00, NULL, 35.00, 9850000.00, true, '2024-01-01');

-- Thêm Loại Phụ Cấp
INSERT INTO allowance_types (id, name, is_taxable, is_insurance_based) VALUES
('dd0e8400-e29b-41d4-a716-446655440000'::uuid, 'Phụ Cấp Trách Nhiệm', true, false),
('dd0e8400-e29b-41d4-a716-446655440001'::uuid, 'Phụ Cấp Ăn Uống', true, false),
('dd0e8400-e29b-41d4-a716-446655440002'::uuid, 'Phụ Cấp Đi Lại', true, false),
('dd0e8400-e29b-41d4-a716-446655440003'::uuid, 'Phụ Cấp Điện Thoại', true, false),
('dd0e8400-e29b-41d4-a716-446655440004'::uuid, 'Phụ Cấp Nhà Ở', false, false),
('dd0e8400-e29b-41d4-a716-446655440005'::uuid, 'Tiền Làm Thêm Giờ', true, false);
 
-- Thêm Ca Làm Việc
INSERT INTO work_shifts (id, name, start_time, end_time, break_start, break_end, late_grace_minutes, early_leave_grace_minutes) VALUES
('ee0e8400-e29b-41d4-a716-446655440000'::uuid, 'Ca Tiêu Chuẩn', '08:00:00', '17:00:00', '12:00:00', '13:00:00', 15, 0),
('ee0e8400-e29b-41d4-a716-446655440001'::uuid, 'Ca Sáng', '06:00:00', '14:00:00', '10:00:00', '10:30:00', 10, 0),
('ee0e8400-e29b-41d4-a716-446655440002'::uuid, 'Ca Chiều', '14:00:00', '22:00:00', '18:00:00', '18:30:00', 10, 0),
('ee0e8400-e29b-41d4-a716-446655440003'::uuid, 'Ca Đêm', '22:00:00', '06:00:00', '02:00:00', '02:30:00', 15, 15);

-- Thêm Hợp Đồng
INSERT INTO contracts (id, employee_id, contract_number, base_salary, actual_salary, position_id, start_date, end_date, status, is_net) VALUES
('ff0e8400-e29b-41d4-a716-446655440000'::uuid, '990e8400-e29b-41d4-a716-446655440000'::uuid, 'HD-001', 15000000.00, 15000000.00, '660e8400-e29b-41d4-a716-446655440003'::uuid, '2024-01-01', '2026-12-31', 'ACTIVE', false),
('ff0e8400-e29b-41d4-a716-446655440001'::uuid, '990e8400-e29b-41d4-a716-446655440001'::uuid, 'HD-002', 12000000.00, 12000000.00, '660e8400-e29b-41d4-a716-446655440001'::uuid, '2024-01-01', '2026-12-31', 'ACTIVE', false),
('ff0e8400-e29b-41d4-a716-446655440002'::uuid, '990e8400-e29b-41d4-a716-446655440002'::uuid, 'HD-003', 18000000.00, 18000000.00, '660e8400-e29b-41d4-a716-446655440006'::uuid, '2024-01-01', '2026-12-31', 'ACTIVE', false),
('ff0e8400-e29b-41d4-a716-446655440003'::uuid, '990e8400-e29b-41d4-a716-446655440003'::uuid, 'HD-004', 10000000.00, 10000000.00, '660e8400-e29b-41d4-a716-446655440008'::uuid, '2024-01-01', '2026-12-31', 'ACTIVE', false),
('ff0e8400-e29b-41d4-a716-446655440004'::uuid, '990e8400-e29b-41d4-a716-446655440004'::uuid, 'HD-005', 14000000.00, 14000000.00, '660e8400-e29b-41d4-a716-446655440004'::uuid, '2024-01-01', '2026-12-31', 'ACTIVE', false);

-- Thêm Phụ Cấp Hợp Đồng
INSERT INTO contract_allowances (id, contract_id, allowance_type_id, amount) VALUES
('330e8400-e29b-41d4-a716-446655440000'::uuid, 'ff0e8400-e29b-41d4-a716-446655440000'::uuid, 'dd0e8400-e29b-41d4-a716-446655440000'::uuid, 4000000.00), -- Nguyễn Văn A: Phụ Cấp Trách Nhiệm
('330e8400-e29b-41d4-a716-446655440001'::uuid, 'ff0e8400-e29b-41d4-a716-446655440000'::uuid, 'dd0e8400-e29b-41d4-a716-446655440001'::uuid, 1000000.00), -- Nguyễn Văn A: Phụ Cấp Ăn Uống
('330e8400-e29b-41d4-a716-446655440002'::uuid, 'ff0e8400-e29b-41d4-a716-446655440001'::uuid, 'dd0e8400-e29b-41d4-a716-446655440001'::uuid, 1000000.00), -- Trần Thị B: Phụ Cấp Ăn Uống
('330e8400-e29b-41d4-a716-446655440003'::uuid, 'ff0e8400-e29b-41d4-a716-446655440002'::uuid, 'dd0e8400-e29b-41d4-a716-446655440000'::uuid, 5000000.00), -- Lê Văn C: Phụ Cấp Trách Nhiệm
('330e8400-e29b-41d4-a716-446655440004'::uuid, 'ff0e8400-e29b-41d4-a716-446655440002'::uuid, 'dd0e8400-e29b-41d4-a716-446655440003'::uuid, 500000.00), -- Lê Văn C: Phụ Cấp Điện Thoại
('330e8400-e29b-41d4-a716-446655440005'::uuid, 'ff0e8400-e29b-41d4-a716-446655440003'::uuid, 'dd0e8400-e29b-41d4-a716-446655440001'::uuid, 800000.00), -- Phạm Thị D: Phụ Cấp Ăn Uống
('330e8400-e29b-41d4-a716-446655440006'::uuid, 'ff0e8400-e29b-41d4-a716-446655440004'::uuid, 'dd0e8400-e29b-41d4-a716-446655440000'::uuid, 3500000.00); -- Hoàng Văn E: Phụ Cấp Trách Nhiệm

-- Thêm Chấm Công Hàng Ngày (mẫu cho tháng hiện tại)
INSERT INTO daily_attendances (id, employee_id, date, shift_id, check_in_time, check_out_time, late_minutes, early_minutes, working_hours, status) VALUES
('440e8400-e29b-41d4-a716-446655440000'::uuid, '990e8400-e29b-41d4-a716-446655440000'::uuid, '2026-03-01', 'ee0e8400-e29b-41d4-a716-446655440000'::uuid, '2026-03-01 08:00:00', '2026-03-01 17:00:00', 0, 0, 8.0, 'OK'),
('440e8400-e29b-41d4-a716-446655440001'::uuid, '990e8400-e29b-41d4-a716-446655440000'::uuid, '2026-03-02', 'ee0e8400-e29b-41d4-a716-446655440000'::uuid, '2026-03-02 08:15:00', '2026-03-02 17:00:00', 15, 0, 8.0, 'LATE'),
('440e8400-e29b-41d4-a716-446655440002'::uuid, '990e8400-e29b-41d4-a716-446655440001'::uuid, '2026-03-01', 'ee0e8400-e29b-41d4-a716-446655440000'::uuid, '2026-03-01 08:00:00', '2026-03-01 17:00:00', 0, 0, 8.0, 'OK'),
('440e8400-e29b-41d4-a716-446655440003'::uuid, '990e8400-e29b-41d4-a716-446655440002'::uuid, '2026-03-01', 'ee0e8400-e29b-41d4-a716-446655440000'::uuid, '2026-03-01 08:00:00', '2026-03-01 16:30:00', 0, 30, 7.5, 'EARLY'),
('440e8400-e29b-41d4-a716-446655440004'::uuid, '990e8400-e29b-41d4-a716-446655440003'::uuid, '2026-03-01', 'ee0e8400-e29b-41d4-a716-446655440000'::uuid, NULL, NULL, 0, 0, 0.0, 'ABSENT'),
('440e8400-e29b-41d4-a716-446655440005'::uuid, '990e8400-e29b-41d4-a716-446655440004'::uuid, '2026-03-01', 'ee0e8400-e29b-41d4-a716-446655440000'::uuid, '2026-03-01 08:00:00', '2026-03-01 17:00:00', 0, 0, 8.0, 'OK');

-- Lưu ý: Trong production, thay thế hash mật khẩu mẫu bằng hash BCrypt thực tế
-- Bạn có thể tạo hash BCrypt bằng công cụ online hoặc Spring Security's BCryptPasswordEncoder