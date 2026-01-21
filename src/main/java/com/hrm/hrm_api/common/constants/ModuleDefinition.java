package com.hrm.hrm_api.common.constants;

public enum ModuleDefinition {
    EMPLOYEE("EMPLOYEE", "Quản lý nhân viên", "Quản lý thông tin nhân viên", "/employee"),
    CONTRACT("CONTRACT", "Quản lý hợp đồng", "Quản lý hợp đồng lao động", "/contract"),
    SALARY("SALARY", "Quản lý lương", "Quản lý cấu trúc lương", "/salary"),
    ATTENDANCE("ATTENDANCE", "Quản lý chấm công", "Quản lý điểm danh và chấm công", "/attendance"),
    PAYROLL("PAYROLL", "Quản lý bảng lương", "Quản lý tính lương và phiếu lương", "/payroll"),
    ROLE("ROLE", "Quản lý vai trò", "Quản lý vai trò trong hệ thống", "/role"),
    PERMISSION("PERMISSION", "Phân quyền", "Quản lý quyền hạn", "/permission");

    private final String code;
    private final String name;
    private final String description;
    private final String url;

    ModuleDefinition(String code, String name, String description, String url) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}