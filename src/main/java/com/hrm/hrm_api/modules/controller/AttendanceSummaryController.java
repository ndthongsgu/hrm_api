package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import com.hrm.hrm_api.modules.service.attendancesummary.AttendanceSummaryService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance-summaries")
public class AttendanceSummaryController {

    private final AttendanceSummaryService service;

    public AttendanceSummaryController(AttendanceSummaryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<AttendanceSummary>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<AttendanceSummary> page = service.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AttendanceSummary>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<AttendanceSummary>> create(@Valid @RequestBody AttendanceSummary payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<AttendanceSummary>> update(@PathVariable UUID id, @Valid @RequestBody AttendanceSummary payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }

    /**
     * Bước 2: Duyệt công - đóng băng công cho kỳ
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<BaseResponse<String>> approveAttendance(@PathVariable UUID id) {
        service.approveAttendance(id);
        return ResponseEntity.ok(BaseResponse.ok("Duyệt công thành công"));
    }
}