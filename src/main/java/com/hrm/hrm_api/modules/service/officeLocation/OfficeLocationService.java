package com.hrm.hrm_api.modules.service.officeLocation;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.OfficeLocationSearchRequest;
import com.hrm.hrm_api.modules.entity.OfficeLocation;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OfficeLocationService {
    PaginationResponse<OfficeLocation> search(OfficeLocationSearchRequest request, Pageable pageable);
    
    OfficeLocation findById(UUID id);
    
    OfficeLocation create(OfficeLocation request);
    
    OfficeLocation update(UUID id, OfficeLocation request);
    
    void deactivate(UUID id);
    
    void activate(UUID id);
    
    void approve(UUID id);
    
    void unApprove(UUID id);
    
    List<SelectOptionDto> getSelectOptions();
}
