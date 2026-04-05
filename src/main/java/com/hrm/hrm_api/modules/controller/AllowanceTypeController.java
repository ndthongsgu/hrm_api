package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.dto.request.AllowanceTypeRequestDto;
import com.hrm.hrm_api.modules.dto.request.AllowanceTypeSearchRequest;
import com.hrm.hrm_api.modules.dto.response.AllowanceTypeResponseDto;
import com.hrm.hrm_api.modules.entity.AllowanceType;
import com.hrm.hrm_api.modules.service.allowancetype.AllowanceTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<BaseResponse<PageResponse<AllowanceTypeResponseDto>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<AllowanceType> page = allowanceTypeService.findAll(pageable);
        PageResponse<AllowanceTypeResponseDto> response = PageResponse.of(
                page.getContent().stream().map(AllowanceTypeResponseDto::fromEntity).toList(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<AllowanceTypeResponseDto>>> search(@RequestBody(required = false) AllowanceTypeSearchRequest request) {
        if (request == null) {
            request = new AllowanceTypeSearchRequest();
        }
        int take = request.getTake() != null && request.getTake() > 0 ? request.getTake() : 20;
        int skip = request.getSkip() != null && request.getSkip() >= 0 ? request.getSkip() : 0;

        Page<AllowanceType> page = allowanceTypeService.search(
                PageRequest.of(skip / take, take),
                request.getWhere()
        );

        PageResponse<AllowanceTypeResponseDto> response = PageResponse.of(
                page.getContent().stream().map(AllowanceTypeResponseDto::fromEntity).toList(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AllowanceTypeResponseDto>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(AllowanceTypeResponseDto.fromEntity(allowanceTypeService.findById(id))));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<AllowanceTypeResponseDto>> create(@Valid @RequestBody AllowanceTypeRequestDto payload) {
        return ResponseEntity.ok(BaseResponse.ok(allowanceTypeService.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<AllowanceTypeResponseDto>> update(@PathVariable java.util.UUID id, @Valid @RequestBody AllowanceTypeRequestDto payload) {
        return ResponseEntity.ok(BaseResponse.ok(allowanceTypeService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        allowanceTypeService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}