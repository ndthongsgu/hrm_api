-- V2: Create full payroll / timekeeping / permission schema

-- System settings
CREATE TABLE IF NOT EXISTS sys_settings (
    id BIGSERIAL PRIMARY KEY,
    key_name VARCHAR(255) UNIQUE NOT NULL,
    value NUMERIC(15,2),
    description TEXT,
    effective_date DATE,
    created_at TIMESTAMP DEFAULT now()
);

-- Roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    permissions_json JSONB
);

-- Note: `permissions` table removed; permissions are stored inside `roles.permissions_json`.

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    active BOOLEAN DEFAULT true,
    employee_id BIGINT REFERENCES employees(id),
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- Tax brackets
CREATE TABLE IF NOT EXISTS tax_brackets (
    id BIGSERIAL PRIMARY KEY,
    policy_name VARCHAR(255),
    min_income NUMERIC(15,2),
    max_income NUMERIC(15,2),
    tax_rate DOUBLE PRECISION,
    subtract_amount NUMERIC(15,2),
    is_active BOOLEAN DEFAULT true
);

-- Insurance configs
CREATE TABLE IF NOT EXISTS insurance_configs (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50),
    employee_rate NUMERIC(5,2),
    company_rate DOUBLE PRECISION,
    cap_base_type VARCHAR(50),
    cap_multiplier INTEGER
);

-- Departments & positions
CREATE TABLE IF NOT EXISTS departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    code VARCHAR(100),
    parent_id BIGINT REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS positions (
    id BIGSERIAL PRIMARY KEY,
    department_id BIGINT REFERENCES departments(id),
    name VARCHAR(255),
    code VARCHAR(100),
    standard_responsibility_allowance NUMERIC(15,2)
);

-- Employees table (kept minimal, extended in entities)
CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255),
    employee_code VARCHAR(100) UNIQUE,
    email VARCHAR(255),
    phone VARCHAR(50),
    department_id BIGINT REFERENCES departments(id),
    position_id BIGINT REFERENCES positions(id),
    tax_code VARCHAR(255),
    bank_account VARCHAR(255),
    bank_name VARCHAR(255),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT now()
);

-- Dependents
CREATE TABLE IF NOT EXISTS dependents (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    full_name VARCHAR(255),
    relationship VARCHAR(255),
    birth_date DATE,
    is_qualified BOOLEAN DEFAULT true
);

-- Contracts
CREATE TABLE IF NOT EXISTS contracts (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    contract_number VARCHAR(255),
    base_salary NUMERIC(15,2),
    actual_salary NUMERIC(15,2),
    position_id BIGINT REFERENCES positions(id),
    start_date DATE,
    end_date DATE,
    status VARCHAR(50),
    is_net BOOLEAN
);

-- Allowance types and contract allowances
CREATE TABLE IF NOT EXISTS allowance_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    is_taxable BOOLEAN,
    is_insurance_based BOOLEAN
);

CREATE TABLE IF NOT EXISTS contract_allowances (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT REFERENCES contracts(id),
    allowance_type_id BIGINT REFERENCES allowance_types(id),
    amount NUMERIC(15,2)
);

-- Payroll periods and attendance summaries
CREATE TABLE IF NOT EXISTS payroll_periods (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    standard_working_days REAL,
    is_locked BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS attendance_summaries (
    id BIGSERIAL PRIMARY KEY,
    payroll_period_id BIGINT REFERENCES payroll_periods(id),
    employee_id BIGINT REFERENCES employees(id),
    actual_working_days REAL,
    paid_leave_days REAL,
    unpaid_leave_days REAL,
    overtime_hours REAL
);

-- Salary adjustments
CREATE TABLE IF NOT EXISTS salary_adjustments (
    id BIGSERIAL PRIMARY KEY,
    payroll_period_id BIGINT REFERENCES payroll_periods(id),
    employee_id BIGINT REFERENCES employees(id),
    amount NUMERIC(15,2),
    type VARCHAR(50),
    reason TEXT,
    is_taxable BOOLEAN DEFAULT true,
    is_net_only BOOLEAN DEFAULT false
);

-- Payslips
CREATE TABLE IF NOT EXISTS payslips (
    id BIGSERIAL PRIMARY KEY,
    payroll_period_id BIGINT REFERENCES payroll_periods(id),
    employee_id BIGINT REFERENCES employees(id),
    contract_id BIGINT REFERENCES contracts(id),

    gross_salary NUMERIC(15,2),
    salary_by_workdays NUMERIC(15,2),
    total_allowance NUMERIC(15,2),
    total_bonus NUMERIC(15,2),

    bhxh_salary_base NUMERIC(15,2),
    bhxh_amount NUMERIC(15,2),
    bhyt_amount NUMERIC(15,2),
    bhtn_amount NUMERIC(15,2),
    social_insurance_benefit_amount NUMERIC(15,2),

    taxable_income NUMERIC(15,2),
    deduction_personal NUMERIC(15,2),
    deduction_dependents NUMERIC(15,2),
    assessable_income NUMERIC(15,2),
    tax_amount NUMERIC(15,2),

    net_salary NUMERIC(15,2),
    created_at TIMESTAMP DEFAULT now()
);

-- Timekeeping & devices
CREATE TABLE IF NOT EXISTS timekeeping_devices (
    id BIGSERIAL PRIMARY KEY,
    device_name VARCHAR(255),
    device_code VARCHAR(255) UNIQUE,
    device_type VARCHAR(100),
    ip_address VARCHAR(100),
    location VARCHAR(255),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS employee_identities (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    identity_type VARCHAR(100),
    identity_value TEXT,
    image_url TEXT,
    is_active BOOLEAN DEFAULT true,
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS work_shifts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    start_time TIME,
    end_time TIME,
    break_start TIME,
    break_end TIME,
    late_grace_minutes INTEGER DEFAULT 15,
    early_leave_grace_minutes INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS employee_shift_assignments (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    shift_id BIGINT REFERENCES work_shifts(id),
    start_date DATE,
    end_date DATE
);

CREATE TABLE IF NOT EXISTS time_logs (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    device_id BIGINT REFERENCES timekeeping_devices(id),
    check_time TIMESTAMP,
    type VARCHAR(50),
    method VARCHAR(50),
    confidence_score REAL,
    snapshot_url TEXT,
    is_processed BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS daily_attendances (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    date DATE,
    shift_id BIGINT REFERENCES work_shifts(id),
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    late_minutes INTEGER,
    early_minutes INTEGER,
    working_hours REAL,
    status VARCHAR(50)
);
