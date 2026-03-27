package com.hrm.hrm_api.modules.service.workshift;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.WorkShiftSearchRequest;
import com.hrm.hrm_api.modules.entity.WorkShift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface WorkShiftService {
    Page<WorkShift> findAll(Pageable pageable);
    WorkShift findById(UUID id);
    WorkShift create(WorkShift entity);
    WorkShift update(UUID id, WorkShift entity);
    void delete(UUID id);
    
    /**
     * Search for work shifts with filtering and pagination
     * 
     * @param request Search request with filters
     * @param pageable Pagination information
     * @return Paginated response of work shifts
     */
    PaginationResponse<WorkShift> search(WorkShiftSearchRequest request, Pageable pageable);
    
    /**
     * Get all work shifts as select options
     * 
     * @return List of select options for work shifts
     */
    List<SelectOptionDto> getSelectOptions();
}