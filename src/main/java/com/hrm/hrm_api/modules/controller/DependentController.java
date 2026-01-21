package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.Dependent;
import com.hrm.hrm_api.modules.service.dependent.DependentService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dependents")
public class DependentController {

    private final DependentService dependentService;

    public DependentController(DependentService dependentService) {
        this.dependentService = dependentService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<Dependent>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<Dependent> page = dependentService.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Dependent>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(dependentService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Dependent>> create(@Valid @RequestBody Dependent payload) {
        return ResponseEntity.ok(BaseResponse.ok(dependentService.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Dependent>> update(@PathVariable java.util.UUID id, @Valid @RequestBody Dependent payload) {
        return ResponseEntity.ok(BaseResponse.ok(dependentService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        dependentService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}