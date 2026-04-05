package com.hrm.hrm_api.modules.service.allowancetype;

import com.hrm.hrm_api.modules.dto.request.AllowanceTypeRequestDto;
import com.hrm.hrm_api.modules.dto.response.AllowanceTypeResponseDto;
import com.hrm.hrm_api.modules.dto.allowancetype.AllowanceTypeFilter;
import com.hrm.hrm_api.modules.entity.AllowanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AllowanceTypeService {
    Page<AllowanceType> search(Pageable pageable, AllowanceTypeFilter filter);
    Page<AllowanceType> findAll(Pageable pageable);
    AllowanceType findById(UUID id);
    AllowanceTypeResponseDto create(AllowanceTypeRequestDto request);
    AllowanceTypeResponseDto update(UUID id, AllowanceTypeRequestDto request);
    void delete(UUID id);
}