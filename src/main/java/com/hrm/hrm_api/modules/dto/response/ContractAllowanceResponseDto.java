package com.hrm.hrm_api.modules.dto.response;

import com.hrm.hrm_api.modules.entity.AllowanceType;
import com.hrm.hrm_api.modules.entity.ContractAllowance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractAllowanceResponseDto {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
    private Boolean isDeleted;

    private ContractResponseDto contract;
    private AllowanceTypeRef allowanceType;
    private BigDecimal amount;

    public static ContractAllowanceResponseDto fromEntity(ContractAllowance entity) {
        if (entity == null) {
            return null;
        }

        return ContractAllowanceResponseDto.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .version(entity.getVersion())
                .isDeleted(entity.getIsDeleted())
                .contract(ContractResponseDto.fromEntity(entity.getContract()))
                .allowanceType(AllowanceTypeRef.fromEntity(entity.getAllowanceType()))
                .amount(entity.getAmount())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AllowanceTypeRef {
        private UUID id;
        private String name;
        private Boolean isTaxable;
        private Boolean isInsuranceBased;

        public static AllowanceTypeRef fromEntity(AllowanceType entity) {
            if (entity == null) {
                return null;
            }
            return AllowanceTypeRef.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .isTaxable(entity.getIsTaxable())
                    .isInsuranceBased(entity.getIsInsuranceBased())
                    .build();
        }
    }
}