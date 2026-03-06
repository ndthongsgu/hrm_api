package com.hrm.hrm_api.modules.service.actionlog;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.ActionLogSearchRequest;
import com.hrm.hrm_api.modules.dto.response.ActionLogResponse;
import com.hrm.hrm_api.modules.entity.ActionLog;
import com.hrm.hrm_api.modules.repository.ActionLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ActionLogServiceImpl implements ActionLogService {

    private final ActionLogRepository actionLogRepository;
    private static final Logger logger = Logger.getLogger(ActionLogServiceImpl.class.getName());

    public ActionLogServiceImpl(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    @Override
    public Page<ActionLog> findByFilters(String functionType, String functionId, String type, String createdBy, Pageable pageable) {
        return actionLogRepository.findByFilters(functionType, functionId, type, createdBy, pageable);
    }

    @Override
    @Transactional
    public ActionLog create(ActionLog actionLog) {
        return actionLogRepository.save(actionLog);
    }

    @Override
    public ActionLog findById(UUID id) {
        return actionLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ActionLog.class, id));
    }

    @Override
    public PaginationResponse<ActionLogResponse> search(ActionLogSearchRequest request) {
        try {
            // Set defaults if request is null
            if (request == null) {
                request = new ActionLogSearchRequest();
                request.setSkip(0);
                request.setTake(20);
            }

            // Set defaults for skip/take if not provided
            int skip = request.getSkip() != null ? request.getSkip() : 0;
            int take = request.getTake() != null && request.getTake() > 0 ? request.getTake() : 20;

            // Validate skip and take
            if (skip < 0) skip = 0;
            if (take <= 0) take = 20;
            if (take > 100) take = 100; // Max page size

            // Convert skip/take format to page/size format
            // skip is 0-based, take is page size
            int pageNumber = skip / take;
            int pageSize = take;

            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

            ActionLogSearchRequest.ActionLogFilterRequest filter = request.getWhere();

            Page<ActionLog> actionLogs = findByFilters(
                filter != null ? filter.getFunctionType() : null,
                filter != null ? filter.getFunctionId() : null,
                filter != null ? filter.getType() : null,
                filter != null ? filter.getCreatedBy() : null,
                pageable
            );

            List<ActionLogResponse> responses = actionLogs.getContent().stream()
                    .map(this::mapToResponse)
                    .toList();

            return PaginationResponse.of(
                    responses,
                    actionLogs.getTotalElements(),
                    pageNumber,
                    pageSize
            );
        } catch (Exception e) {
            logger.severe("Error in ActionLog search: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error searching action logs: " + e.getMessage(), e);
        }
    }

    private ActionLogResponse mapToResponse(ActionLog actionLog) {
        return ActionLogResponse.builder()
                .id(actionLog.getId())
                .functionId(actionLog.getFunctionId())
                .functionType(actionLog.getFunctionType())
                .type(actionLog.getType())
                .description(actionLog.getDescription())
                .createdByName(actionLog.getCreatedByName())
                .employeeCode(actionLog.getEmployeeCode())
                .createdAt(actionLog.getCreatedAt())
                .createdBy(actionLog.getCreatedBy())
                .updatedAt(actionLog.getUpdatedAt())
                .updatedBy(actionLog.getUpdatedBy())
                .action(actionLog.getType())
                .build();
    }
}
