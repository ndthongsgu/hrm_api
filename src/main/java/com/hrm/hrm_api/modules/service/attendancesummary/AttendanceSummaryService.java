package com.hrm.hrm_api.modules.service.attendancesummary;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummaryCreateRequest;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummarySearchRequest;
import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AttendanceSummaryService {
    PaginationResponse<AttendanceSummary> search(AttendanceSummarySearchRequest request, Pageable pageable);
    AttendanceSummary findById(UUID id);
    void delete(UUID id);
    AttendanceSummary update(UUID id, AttendanceSummaryCreateRequest request);
    void deactivate(UUID id);
    void activate(UUID id);
    void approve(UUID id);
    void unApprove(UUID id);
    String bulkApprove(List<UUID> ids);
    String bulkUnApprove(List<UUID> ids);
    List<UUID> getIdsByFilter(AttendanceSummarySearchRequest request);
}