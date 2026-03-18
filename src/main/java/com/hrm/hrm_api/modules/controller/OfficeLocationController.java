package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.common.constants.PermissionDefinition;
import com.hrm.hrm_api.config.annotations.RequirePermission;
import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.OfficeLocationSearchRequest;
import com.hrm.hrm_api.modules.entity.OfficeLocation;
import com.hrm.hrm_api.modules.service.officeLocation.OfficeLocationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/office-locations")
public class OfficeLocationController {

    private final OfficeLocationService service;

    public OfficeLocationController(OfficeLocationService service) {
        this.service = service;
    }

    /**
     * Tìm kiếm địa điểm văn phòng với phân trang
     */
    @PostMapping
    @RequirePermission(module = "OFFICE", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<PaginationResponse<OfficeLocation>>> search(
            @RequestBody(required = false) OfficeLocationSearchRequest request,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PaginationResponse<OfficeLocation> response = service.search(request, pageable);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    /**
     * Lấy thông tin địa điểm văn phòng theo ID
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "OFFICE", action = PermissionDefinition.READ)
    public ResponseEntity<BaseResponse<OfficeLocation>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    /**
     * Tạo mới địa điểm văn phòng
     */
    @PostMapping("/create")
    @RequirePermission(module = "OFFICE", action = PermissionDefinition.CREATE)
    public ResponseEntity<BaseResponse<OfficeLocation>> create(@Valid @RequestBody OfficeLocation request) {
        OfficeLocation created = service.create(request);
        return ResponseEntity.ok(BaseResponse.ok(created));
    }

    /**
     * Cập nhật thông tin địa điểm văn phòng
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "OFFICE", action = PermissionDefinition.UPDATE)
    public ResponseEntity<BaseResponse<OfficeLocation>> update(@PathVariable UUID id, @Valid @RequestBody OfficeLocation request) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, request)));
    }

    /**
     * Ngừng hoạt động địa điểm văn phòng (Soft delete)
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "OFFICE", action = PermissionDefinition.DEACTIVATE)
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(BaseResponse.ok("Ngừng hoạt động địa điểm văn phòng thành công"));
    }

    /**
     * Kích hoạt lại địa điểm văn phòng (Soft delete recovery)
     */
    @PutMapping("/{id}/activate")
    @RequirePermission(module = "OFFICE", action = PermissionDefinition.ACTIVATE)
    public ResponseEntity<BaseResponse<String>> activate(@PathVariable UUID id) {
        service.activate(id);
        return ResponseEntity.ok(BaseResponse.ok("Kích hoạt lại địa điểm văn phòng thành công"));
    }

    /**
     * Duyệt địa điểm văn phòng
     */
    @PostMapping("/{id}/approve")
    @RequirePermission(module = "OFFICE", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> approve(@PathVariable UUID id) {
        service.approve(id);
        return ResponseEntity.ok(BaseResponse.ok("Duyệt địa điểm văn phòng thành công"));
    }

    /**
     * Hủy duyệt địa điểm văn phòng
     */
    @PostMapping("/{id}/unApprove")
    @RequirePermission(module = "OFFICE", action = PermissionDefinition.APPROVE)
    public ResponseEntity<BaseResponse<String>> unApprove(@PathVariable UUID id) {
        service.unApprove(id);
        return ResponseEntity.ok(BaseResponse.ok("Hủy duyệt địa điểm văn phòng thành công"));
    }
}
