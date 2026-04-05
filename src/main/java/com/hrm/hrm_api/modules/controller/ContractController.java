package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.dto.request.ContractSearchRequest;
import com.hrm.hrm_api.modules.dto.request.ContractRequestDto;
import com.hrm.hrm_api.modules.dto.response.ContractResponseDto;
import com.hrm.hrm_api.modules.entity.Contract;
import com.hrm.hrm_api.modules.service.contract.ContractService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<ContractResponseDto>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<Contract> page = contractService.findAll(pageable);
        List<ContractResponseDto> items = page.getContent().stream()
                .map(ContractResponseDto::fromEntity)
                .toList();
        PageResponse<ContractResponseDto> response = PageResponse.of(
                items,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<ContractResponseDto>>> search(@RequestBody(required = false) ContractSearchRequest request) {
        if (request == null) {
            request = new ContractSearchRequest();
        }
        int take = request.getTake() != null && request.getTake() > 0 ? request.getTake() : 20;
        int skip = request.getSkip() != null && request.getSkip() >= 0 ? request.getSkip() : 0;

        Page<Contract> page = contractService.search(
                PageRequest.of(skip / take, take),
                request.getWhere()
        );

        PageResponse<ContractResponseDto> response = PageResponse.of(
                page.getContent().stream().map(ContractResponseDto::fromEntity).toList(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ContractResponseDto>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(ContractResponseDto.fromEntity(contractService.findById(id))));
    }

    @GetMapping("/active")
    public ResponseEntity<BaseResponse<ContractResponseDto>> getActiveContract(
            @RequestParam UUID employeeId,
            @RequestParam(required = false) UUID payrollPeriodId,
            @RequestParam(required = false) LocalDate date
    ) {
        Optional<Contract> contractOpt;
        if (payrollPeriodId != null) {
            contractOpt = contractService.findActiveByPayrollPeriod(employeeId, payrollPeriodId);
        } else {
            contractOpt = contractService.findActiveByDate(employeeId, date == null ? LocalDate.now() : date);
        }
        return ResponseEntity.ok(BaseResponse.ok(contractOpt.map(ContractResponseDto::fromEntity).orElse(null)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ContractResponseDto>> create(@Valid @RequestBody ContractRequestDto payload) {
        return ResponseEntity.ok(BaseResponse.ok(ContractResponseDto.fromEntity(contractService.create(payload))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ContractResponseDto>> update(@PathVariable java.util.UUID id, @Valid @RequestBody ContractRequestDto payload) {
        return ResponseEntity.ok(BaseResponse.ok(ContractResponseDto.fromEntity(contractService.update(id, payload))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        contractService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}