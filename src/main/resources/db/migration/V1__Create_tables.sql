-- Create employees table
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(255) UNIQUE NOT NULL,
    position VARCHAR(255),
    department VARCHAR(255),
    hire_date DATE,
    active BOOLEAN DEFAULT TRUE,
    password VARCHAR(255)
);

-- Create labor_contracts table
CREATE TABLE labor_contracts (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    contract_type VARCHAR(50),
    start_date DATE,
    end_date DATE,
    basic_salary DECIMAL(15,2),
    terms TEXT
);

-- Create salary_structures table
CREATE TABLE salary_structures (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    basic_salary DECIMAL(15,2),
    housing_allowance DECIMAL(15,2) DEFAULT 0,
    transport_allowance DECIMAL(15,2) DEFAULT 0,
    other_allowances DECIMAL(15,2) DEFAULT 0,
    bhxh_rate DECIMAL(5,4) DEFAULT 0.08,
    bhyt_rate DECIMAL(5,4) DEFAULT 0.015,
    bhtn_rate DECIMAL(5,4) DEFAULT 0.01
);

-- Create attendances table
CREATE TABLE attendances (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    hours_worked DECIMAL(5,2),
    status VARCHAR(50)
);

-- Create payrolls table
CREATE TABLE payrolls (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT REFERENCES employees(id),
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    gross_salary DECIMAL(15,2),
    bhxh_deduction DECIMAL(15,2),
    bhyt_deduction DECIMAL(15,2),
    bhtn_deduction DECIMAL(15,2),
    tax_deduction DECIMAL(15,2),
    net_salary DECIMAL(15,2),
    working_days INTEGER
);

-- Create permissions table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    module VARCHAR(50) NOT NULL,
    action VARCHAR(20) NOT NULL,
    description TEXT,
    UNIQUE(module, action)
);

-- Create roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Create role_permissions table
CREATE TABLE role_permissions (
    role_id BIGINT REFERENCES roles(id),
    permission_id BIGINT REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

-- Create user_roles table
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES employees(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
