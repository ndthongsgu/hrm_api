package com.hrm.hrm_api.modules.dto.response;

import com.hrm.hrm_api.modules.entity.Contract;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractResponseDto {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
    private Boolean isDeleted;

    private String contractNumber;
    private BigDecimal baseSalary;
    private BigDecimal actualSalary;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean isNet;

    private EmployeeRef employee;
    private PositionRef position;

    public static ContractResponseDto fromEntity(Contract entity) {
        if (entity == null) {
            return null;
        }

        return ContractResponseDto.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .version(entity.getVersion())
                .isDeleted(entity.getIsDeleted())
                .contractNumber(entity.getContractNumber())
                .baseSalary(entity.getBaseSalary())
                .actualSalary(entity.getActualSalary())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .isNet(entity.getIsNet())
                .employee(EmployeeRef.fromEntity(entity.getEmployee()))
                .position(PositionRef.fromEntity(entity.getPosition()))
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeRef {
        private UUID id;
        private String fullName;
        private String employeeCode;

        public static EmployeeRef fromEntity(Employee entity) {
            if (entity == null) {
                return null;
            }
            return EmployeeRef.builder()
                    .id(entity.getId())
                    .fullName(entity.getFullName())
                    .employeeCode(entity.getEmployeeCode())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionRef {
        private UUID id;
        private String name;
        private String code;

        public static PositionRef fromEntity(Position entity) {
            if (entity == null) {
                return null;
            }
            return PositionRef.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .code(entity.getCode())
                    .build();
        }
    }
}