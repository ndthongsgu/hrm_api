package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.AllowanceType;
import com.hrm.hrm_api.modules.service.allowancetype.AllowanceTypeService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/allowance-types")
public class AllowanceTypeController {

    private final AllowanceTypeService allowanceTypeService;

    public AllowanceTypeController(AllowanceTypeService allowanceTypeService) {
        this.allowanceTypeService = allowanceTypeService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<AllowanceType>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<AllowanceType> page = allowanceTypeService.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AllowanceType>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.ok(allowanceTypeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<AllowanceType>> create(@Valid @RequestBody AllowanceType payload) {
        return ResponseEntity.ok(BaseResponse.ok(allowanceTypeService.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<AllowanceType>> update(@PathVariable Long id, @Valid @RequestBody AllowanceType payload) {
        return ResponseEntity.ok(BaseResponse.ok(allowanceTypeService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        allowanceTypeService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}