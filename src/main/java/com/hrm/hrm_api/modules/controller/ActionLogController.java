package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.ActionLogSearchRequest;
import com.hrm.hrm_api.modules.dto.response.ActionLogResponse;
import com.hrm.hrm_api.modules.service.actionlog.ActionLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/action-logs")
public class ActionLogController {

    private final ActionLogService actionLogService;

    public ActionLogController(ActionLogService actionLogService) {
        this.actionLogService = actionLogService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<PaginationResponse<ActionLogResponse>>> search(
            @RequestBody(required = false) ActionLogSearchRequest request) {
        PaginationResponse<ActionLogResponse> response = actionLogService.search(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ActionLogResponse>> getById(@PathVariable String id) {
        try {
            java.util.UUID uuid = java.util.UUID.fromString(id);
            var actionLog = actionLogService.findById(uuid);
            var response = ActionLogResponse.builder()
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
            return ResponseEntity.ok(BaseResponse.ok(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Invalid ID format"));
        }
    }
}
