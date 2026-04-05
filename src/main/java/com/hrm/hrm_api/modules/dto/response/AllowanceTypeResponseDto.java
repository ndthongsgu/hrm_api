package com.hrm.hrm_api.modules.dto.response;

import com.hrm.hrm_api.modules.entity.AllowanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllowanceTypeResponseDto {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
    private Boolean isDeleted;

    private String name;
    private Boolean isTaxable;
    private Boolean isInsuranceBased;

    public static AllowanceTypeResponseDto fromEntity(AllowanceType entity) {
        if (entity == null) {
            return null;
        }

        return AllowanceTypeResponseDto.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .version(entity.getVersion())
                .isDeleted(entity.getIsDeleted())
                .name(entity.getName())
                .isTaxable(entity.getIsTaxable())
                .isInsuranceBased(entity.getIsInsuranceBased())
                .build();
    }
}
