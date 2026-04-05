package com.hrm.hrm_api.modules.service.contractallowance;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.contractallowance.ContractAllowanceFilter;
import com.hrm.hrm_api.modules.dto.request.ContractAllowanceRequestDto;
import com.hrm.hrm_api.modules.dto.response.ContractAllowanceResponseDto;
import com.hrm.hrm_api.modules.entity.AllowanceType;
import com.hrm.hrm_api.modules.entity.Contract;
import com.hrm.hrm_api.modules.entity.ContractAllowance;
import com.hrm.hrm_api.modules.repository.AllowanceTypeRepository;
import com.hrm.hrm_api.modules.repository.ContractRepository;
import com.hrm.hrm_api.modules.repository.ContractAllowanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ContractAllowanceServiceImpl implements ContractAllowanceService {

    private final ContractAllowanceRepository contractAllowanceRepository;
    private final ContractRepository contractRepository;
    private final AllowanceTypeRepository allowanceTypeRepository;

    public ContractAllowanceServiceImpl(
            ContractAllowanceRepository repository,
            ContractRepository contractRepository,
            AllowanceTypeRepository allowanceTypeRepository) {
        this.contractAllowanceRepository = repository;
        this.contractRepository = contractRepository;
        this.allowanceTypeRepository = allowanceTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractAllowance> findAll(Pageable pageable) {
        return contractAllowanceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractAllowance> search(Pageable pageable, ContractAllowanceFilter filter) {
        return contractAllowanceRepository.findAll(buildSpec(filter), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractAllowance findById(UUID id) {
        return contractAllowanceRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ContractAllowanceResponseDto create(ContractAllowanceRequestDto request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException(Contract.class, request.getContractId()));
        AllowanceType allowanceType = allowanceTypeRepository.findById(request.getAllowanceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(AllowanceType.class, request.getAllowanceTypeId()));

        ContractAllowance entity = new ContractAllowance();
        entity.setContract(contract);
        entity.setAllowanceType(allowanceType);
        entity.setAmount(request.getAmount());

        return ContractAllowanceResponseDto.fromEntity(contractAllowanceRepository.save(entity));
    }

    @Override
    @Transactional
    public ContractAllowanceResponseDto update(UUID id, ContractAllowanceRequestDto request) {
        ContractAllowance entity = findById(id);
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException(Contract.class, request.getContractId()));
        AllowanceType allowanceType = allowanceTypeRepository.findById(request.getAllowanceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(AllowanceType.class, request.getAllowanceTypeId()));

        entity.setContract(contract);
        entity.setAllowanceType(allowanceType);
        entity.setAmount(request.getAmount());

        return ContractAllowanceResponseDto.fromEntity(contractAllowanceRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        contractAllowanceRepository.deleteById(id);
    }

    @Override
    public List<ContractAllowance> findByContractId(UUID contractId) {
        return contractAllowanceRepository.findByContractId(contractId);
    }

    private Specification<ContractAllowance> buildSpec(ContractAllowanceFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter != null) {
                if (filter.getContractId() != null) {
                    predicates = cb.and(predicates,
                            cb.equal(root.get("contract").get("id"), filter.getContractId()));
                }
                if (filter.getAllowanceTypeId() != null) {
                    predicates = cb.and(predicates,
                            cb.equal(root.get("allowanceType").get("id"), filter.getAllowanceTypeId()));
                }
                if (filter.getEmployeeId() != null) {
                    predicates = cb.and(predicates,
                            cb.equal(root.get("contract").get("employee").get("id"), filter.getEmployeeId()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates,
                            cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
                } else {
                    predicates = cb.and(predicates,
                            cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
                }
            } else {
                predicates = cb.and(predicates,
                        cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
            }

            return predicates;
        };
    }
}   