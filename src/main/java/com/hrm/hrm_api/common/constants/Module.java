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
        for (com.hrm.hrm_api.common.constants.ModuleDefinition def : com.hrm.hrm_api.common.constants.ModuleDefinition.values()) {
            result.add(create(def.getCode(), def.getName(), def.getDescription(), def.getUrl()));
        }
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
