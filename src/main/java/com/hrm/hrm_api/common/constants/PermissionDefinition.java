package com.hrm.hrm_api.common.constants;

public enum PermissionDefinition {
    READ("READ", "Xem", "Cho phép xem dữ liệu"),
    CREATE("CREATE", "Tạo", "Cho phép tạo bản ghi mới"),
    UPDATE("UPDATE", "Cập nhật", "Cho phép chỉnh sửa bản ghi"),
    DELETE("DELETE", "Xóa", "Cho phép xóa bản ghi"),
    ACTIVATE("ACTIVATE", "Kích hoạt", "Cho phép kích hoạt bản ghi"),
    DEACTIVATE("DEACTIVATE", "Hủy kích hoạt", "Cho phép hủy kích hoạt bản ghi"),
    IMPORT("IMPORT", "Nhập khẩu", "Cho phép import dữ liệu"),
    EXPORT("EXPORT", "Xuất khẩu", "Cho phép xuất dữ liệu");

    private final String code;
    private final String name;
    private final String description;

    PermissionDefinition(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
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
}