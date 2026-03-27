package com.hrm.hrm_api.modules.service.employeeshift;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.EmployeeShiftAssignmentCreateRequest;
import com.hrm.hrm_api.modules.dto.request.EmployeeShiftAssignmentSearchRequest;
import com.hrm.hrm_api.modules.dto.response.EmployeeShiftAssignmentDto;
import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EmployeeShiftAssignmentService {
    Page<EmployeeShiftAssignment> findAll(Pageable pageable);
    EmployeeShiftAssignment findById(UUID id);
    EmployeeShiftAssignment create(EmployeeShiftAssignmentCreateRequest request);
    EmployeeShiftAssignment update(UUID id, EmployeeShiftAssignmentCreateRequest request);
    void delete(UUID id);

    /**
     * Search for employee shift assignments with filtering and pagination
     * 
     * @param request Search request with filters
     * @param pageable Pagination information
     * @return Paginated response of employee shift assignments
     */
    PaginationResponse<EmployeeShiftAssignmentDto> search(EmployeeShiftAssignmentSearchRequest request, Pageable pageable);

    /**
     * Get all employee shift assignments as select options
     * 
     * @return List of select options for assignments
     */
    List<SelectOptionDto> getSelectOptions();
}