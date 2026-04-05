package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.dto.request.ContractAllowanceRequestDto;
import com.hrm.hrm_api.modules.dto.request.ContractAllowanceSearchRequest;
import com.hrm.hrm_api.modules.dto.response.ContractAllowanceResponseDto;
import com.hrm.hrm_api.modules.entity.ContractAllowance;
import com.hrm.hrm_api.modules.service.contractallowance.ContractAllowanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contract-allowances")
public class ContractAllowanceController {

    private final ContractAllowanceService service;

    public ContractAllowanceController(ContractAllowanceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<ContractAllowanceResponseDto>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<ContractAllowance> page = service.findAll(pageable);
        List<ContractAllowanceResponseDto> items = page.getContent().stream()
                .map(ContractAllowanceResponseDto::fromEntity)
                .toList();
        PageResponse<ContractAllowanceResponseDto> response = PageResponse.of(
                items,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<ContractAllowanceResponseDto>>> search(@RequestBody(required = false) ContractAllowanceSearchRequest request) {
        if (request == null) {
            request = new ContractAllowanceSearchRequest();
        }
        int take = request.getTake() != null && request.getTake() > 0 ? request.getTake() : 20;
        int skip = request.getSkip() != null && request.getSkip() >= 0 ? request.getSkip() : 0;

        Page<ContractAllowance> page = service.search(
                PageRequest.of(skip / take, take),
                request.getWhere()
        );

        PageResponse<ContractAllowanceResponseDto> response = PageResponse.of(
                page.getContent().stream().map(ContractAllowanceResponseDto::fromEntity).toList(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ContractAllowanceResponseDto>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(ContractAllowanceResponseDto.fromEntity(service.findById(id))));
    }

    @GetMapping("/by-contract/{contractId}")
    public ResponseEntity<BaseResponse<List<ContractAllowanceResponseDto>>> getByContract(@PathVariable UUID contractId) {
        List<ContractAllowanceResponseDto> response = service.findByContractId(contractId).stream()
                .map(ContractAllowanceResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ContractAllowanceResponseDto>> create(@Valid @RequestBody ContractAllowanceRequestDto payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ContractAllowanceResponseDto>> update(@PathVariable java.util.UUID id, @Valid @RequestBody ContractAllowanceRequestDto payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}