package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.common.security.SecurityUtil;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummaryCreateRequest;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummarySearchRequest;
import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import com.hrm.hrm_api.modules.service.attendancesummary.AttendanceSummaryService;
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance-summaries")
public class AttendanceSummaryController {

    private final AttendanceSummaryService service;
    private final ActionLogUtil actionLogUtil;
    private final SecurityUtil securityUtil;

    public AttendanceSummaryController(AttendanceSummaryService service, ActionLogUtil actionLogUtil, SecurityUtil securityUtil) {
        this.service = service;
        this.actionLogUtil = actionLogUtil;
        this.securityUtil = securityUtil;
    }

    @PostMapping
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PaginationResponse<AttendanceSummary>>> search(@RequestBody(required = false) AttendanceSummarySearchRequest request, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PaginationResponse<AttendanceSummary> response = service.search(request, pageable);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<AttendanceSummary>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping("/create")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<AttendanceSummary>> create(@Valid @RequestBody AttendanceSummaryCreateRequest request) {
        AttendanceSummary created = service.createFromRequest(request);

        actionLogUtil.logCreate(
            created.getId(),
            "ATTENDANCE_SUMMARY",
            "Tạo tổng hợp công cho nhân viên",
            securityUtil.getCurrentUsername()
        );

        return ResponseEntity.ok(BaseResponse.ok(created));
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<AttendanceSummary>> update(@PathVariable UUID id, @Valid @RequestBody AttendanceSummaryCreateRequest request) {
        AttendanceSummary updated = service.updateFromRequest(id, request);

        actionLogUtil.logUpdate(
            id,
            "ATTENDANCE_SUMMARY",
            "Cập nhật tổng hợp công",
            securityUtil.getCurrentUsername()
        );

        return ResponseEntity.ok(BaseResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.DELETE)
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable UUID id) {
        AttendanceSummary summary = service.findById(id);
        service.deactivate(id);

        actionLogUtil.logDeactivate(
            id,
            "ATTENDANCE_SUMMARY",
            "Ngừng hoạt động tổng hợp công",
            securityUtil.getCurrentUsername()
        );

        return ResponseEntity.ok(BaseResponse.ok("Ngừng hoạt động tổng hợp công thành công"));
    }

    /**
     * Kích hoạt lại tổng hợp công (Soft delete recovery)
     */
    @PutMapping("/{id}/activate")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<String>> activate(@PathVariable UUID id) {
        AttendanceSummary summary = service.findById(id);
        service.activate(id);

        actionLogUtil.logActivate(
            id,
            "ATTENDANCE_SUMMARY",
            "Kích hoạt lại tổng hợp công",
            securityUtil.getCurrentUsername()
        );

        return ResponseEntity.ok(BaseResponse.ok("Kích hoạt lại tổng hợp công thành công"));
    }

    /**
     * Bước 2: Duyệt công - đóng băng công cho kỳ
     */
    @PostMapping("/{id}/approve")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<String>> approveAttendance(@PathVariable UUID id) {
        service.approveAttendance(id);
        return ResponseEntity.ok(BaseResponse.ok("Duyệt công thành công"));
    }
}