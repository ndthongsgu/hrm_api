package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.PositionSearchRequest;
import com.hrm.hrm_api.modules.entity.Position;
import com.hrm.hrm_api.modules.service.position.PositionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<PaginationResponse<Position>>> search(@RequestBody(required = false) PositionSearchRequest request) {
        PaginationResponse<Position> response = positionService.search(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/select")
    public ResponseEntity<BaseResponse<List<SelectOptionDto>>> getSelectOptions() {
        List<SelectOptionDto> options = positionService.findAll(Pageable.unpaged())
                .getContent()
                .stream()
                .map(position -> new SelectOptionDto(position.getId(), position.getCode() + " - " + position.getName()))
                .toList();
        return ResponseEntity.ok(BaseResponse.ok(options));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    public ResponseEntity<BaseResponse<Position>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(positionService.findById(id)));
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Position>> create(@Valid @RequestBody Position payload) {
        return ResponseEntity.ok(BaseResponse.ok(positionService.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Position>> update(@PathVariable java.util.UUID id, @Valid @RequestBody Position payload) {
        return ResponseEntity.ok(BaseResponse.ok(positionService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        positionService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}