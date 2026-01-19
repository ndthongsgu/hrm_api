package com.hrm.hrm_api.common.constants;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class Module {

    @Data
    public static class ModuleDto {
        public String code;
        public String name;
        public String description;
        public String urlPageView;

        private Boolean isCanRead = true;
        private Boolean isCanCreate = true;
        private Boolean isCanUpdate = true;
        private Boolean isCanDelete = true;
        private Boolean isCanActivate = true;
        private Boolean isCanDeactivate = true;
        private Boolean isCanImport = true;
        private Boolean isCanExport = true;
    }

    public List<ModuleDto> getModuleList() {
        List<ModuleDto> result = new ArrayList<>();
        result.add(create("EMPLOYEE", "Quản lý nhân viên", "Quản lý thông tin nhân viên", "/employee"));
        result.add(create("CONTRACT", "Quản lý hợp đồng", "Quản lý hợp đồng lao động", "/contract"));
        result.add(create("SALARY", "Quản lý lương", "Quản lý cấu trúc lương", "/salary"));
        result.add(create("ATTENDANCE", "Quản lý chấm công", "Quản lý điểm danh và chấm công", "/attendance"));
        result.add(create("PAYROLL", "Quản lý bảng lương", "Quản lý tính lương và phiếu lương", "/payroll"));
        result.add(create("ROLE", "Quản lý vai trò", "Quản lý vai trò trong hệ thống", "/role"));
        result.add(create("PERMISSION", "Phân quyền", "Quản lý quyền hạn", "/permission"));

        return result;
    }

    private ModuleDto create(String code, String name, String description, String urlPageView) {
        ModuleDto dto = new ModuleDto();
        dto.code = code;
        dto.name = name;
        dto.description = description;
        dto.urlPageView = urlPageView;
        return dto;
    }
}
