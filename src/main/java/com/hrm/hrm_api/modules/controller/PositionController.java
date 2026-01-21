package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.Position;
import com.hrm.hrm_api.modules.service.position.PositionService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<Position>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<Position> page = positionService.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Position>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.ok(positionService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Position>> create(@Valid @RequestBody Position payload) {
        return ResponseEntity.ok(BaseResponse.ok(positionService.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Position>> update(@PathVariable Long id, @Valid @RequestBody Position payload) {
        return ResponseEntity.ok(BaseResponse.ok(positionService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        positionService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}