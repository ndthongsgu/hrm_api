package com.hrm.hrm_api.modules.service.actionlog;

import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.ActionLogSearchRequest;
import com.hrm.hrm_api.modules.dto.response.ActionLogResponse;
import com.hrm.hrm_api.modules.entity.ActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ActionLogService {
    Page<ActionLog> findByFilters(String functionType, String functionId, String type, String createdBy, Pageable pageable);
    ActionLog create(ActionLog actionLog);
    ActionLog findById(UUID id);
    PaginationResponse<ActionLogResponse> search(ActionLogSearchRequest request);
}
