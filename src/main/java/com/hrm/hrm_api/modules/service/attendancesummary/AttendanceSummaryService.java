package com.hrm.hrm_api.modules.service.attendancesummary;

import com.hrm.hrm_api.modules.dto.attendancesummary.AttendanceSummaryFilter;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummaryCreateRequest;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummarySearchRequest;
import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AttendanceSummaryService {
    Page<AttendanceSummary> findAll(Pageable pageable);
    AttendanceSummary findById(UUID id);
    void delete(UUID id);
    AttendanceSummary create(AttendanceSummaryCreateRequest request);
    AttendanceSummary update(UUID id, AttendanceSummaryCreateRequest request);
    Page<AttendanceSummary> findAll(AttendanceSummaryFilter filter, Pageable pageable);
    PaginationResponse<AttendanceSummary> search(AttendanceSummarySearchRequest request, Pageable pageable);
    void deactivate(UUID id);
    void activate(UUID id);
    void approve(UUID id);
    void reject(UUID id, String reason);
}