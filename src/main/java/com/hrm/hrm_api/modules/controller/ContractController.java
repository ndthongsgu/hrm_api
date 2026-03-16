package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PageResponse;
import com.hrm.hrm_api.modules.entity.Contract;
import com.hrm.hrm_api.modules.service.contract.ContractService;
import com.hrm.hrm_api.modules.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
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
    public ResponseEntity<BaseResponse<PageResponse<Contract>>> list(@PageableDefault(size = 20) Pageable pageable) {
        Page<Contract> page = contractService.findAll(pageable);
        return ResponseEntity.ok(BaseResponse.ok(PaginationUtils.toPageResponse(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Contract>> getById(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(contractService.findById(id)));
    }

    @GetMapping("/active")
    public ResponseEntity<BaseResponse<Contract>> getActiveContract(
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
        return ResponseEntity.ok(BaseResponse.ok(contractOpt.orElse(null)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Contract>> create(@Valid @RequestBody Contract payload) {
        return ResponseEntity.ok(BaseResponse.ok(contractService.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Contract>> update(@PathVariable java.util.UUID id, @Valid @RequestBody Contract payload) {
        return ResponseEntity.ok(BaseResponse.ok(contractService.update(id, payload)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable java.util.UUID id) {
        contractService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}