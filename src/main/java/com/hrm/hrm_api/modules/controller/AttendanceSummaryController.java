package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummaryCreateRequest;
import com.hrm.hrm_api.modules.dto.request.AttendanceSummarySearchRequest;
import com.hrm.hrm_api.modules.dto.request.BulkIdsRequest;
import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import com.hrm.hrm_api.modules.service.attendancesummary.AttendanceSummaryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance-summaries")
public class AttendanceSummaryController {

    private final AttendanceSummaryService service;

    public AttendanceSummaryController(AttendanceSummaryService service) {
        this.service = service;
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

    @PutMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<AttendanceSummary>> update(@PathVariable UUID id, @Valid @RequestBody AttendanceSummaryCreateRequest request) {
        AttendanceSummary updated = service.update(id, request);
        return ResponseEntity.ok(BaseResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.DELETE)
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(BaseResponse.ok("Ngừng hoạt động tổng hợp công thành công"));
    }

    /**
     * Kích hoạt lại tổng hợp công (Soft delete recovery)
     */
    @PutMapping("/{id}/activate")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.ACTIVATE)
    public ResponseEntity<BaseResponse<String>> activate(@PathVariable UUID id) {
        service.activate(id);
        return ResponseEntity.ok(BaseResponse.ok("Kích hoạt lại tổng hợp công thành công"));
    }

    /**
     * Bước 2: Duyệt công - đóng băng công cho kỳ
     */
    @PostMapping("/{id}/approve")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> approve(@PathVariable UUID id) {
        service.approve(id);
        return ResponseEntity.ok(BaseResponse.ok("Duyệt công thành công"));
    }

     /**
     * Hủy Duyệt công
     */
    @PostMapping("/{id}/unApprove")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> unApprove(@PathVariable UUID id) {
        service.unApprove(id);
        return ResponseEntity.ok(BaseResponse.ok("Hủy duyệt công thành công"));
    }

    /**
     * Duyệt nhiều tổng hợp công theo danh sách
     */
    @PostMapping("/bulk-approve")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> bulkApprove(@RequestBody BulkIdsRequest request) {
        String message = service.bulkApprove(request.getIds());
        return ResponseEntity.ok(BaseResponse.ok(message));
    }

    /**
     * Hủy duyệt nhiều tổng hợp công theo danh sách
     */
    @PostMapping("/bulk-unapprove")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> bulkUnApprove(@RequestBody BulkIdsRequest request) {
        String message = service.bulkUnApprove(request.getIds());
        return ResponseEntity.ok(BaseResponse.ok(message));
    }

    /**
     * Lấy danh sách ID theo filter (không phân trang)
     */
    @PostMapping("/ids-by-filter")
    @RequirePermission(module = "PAYROLL", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<List<UUID>>> getIdsByFilter(@RequestBody(required = false) AttendanceSummarySearchRequest request) {
        List<UUID> ids = service.getIdsByFilter(request);
        return ResponseEntity.ok(BaseResponse.ok(ids));
    }
}