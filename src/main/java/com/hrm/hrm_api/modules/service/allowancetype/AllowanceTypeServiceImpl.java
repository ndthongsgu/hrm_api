package com.hrm.hrm_api.modules.service.allowancetype;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.allowancetype.AllowanceTypeFilter;
import com.hrm.hrm_api.modules.dto.request.AllowanceTypeRequestDto;
import com.hrm.hrm_api.modules.dto.response.AllowanceTypeResponseDto;
import com.hrm.hrm_api.modules.entity.AllowanceType;
import com.hrm.hrm_api.modules.repository.AllowanceTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AllowanceTypeServiceImpl implements AllowanceTypeService {

    private final AllowanceTypeRepository repository;

    public AllowanceTypeServiceImpl(AllowanceTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<AllowanceType> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<AllowanceType> search(Pageable pageable, AllowanceTypeFilter filter) {
        return repository.findAll(buildSpec(filter), pageable);
    }

    @Override
    public AllowanceType findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(AllowanceType.class, id));
    }

    @Override
    @Transactional
    public AllowanceTypeResponseDto create(AllowanceTypeRequestDto request) {
        AllowanceType entity = new AllowanceType();
        entity.setName(request.getName());
        entity.setIsTaxable(request.getIsTaxable());
        entity.setIsInsuranceBased(request.getIsInsuranceBased());
        return AllowanceTypeResponseDto.fromEntity(repository.save(entity));
    }

    @Override
    @Transactional
    public AllowanceTypeResponseDto update(UUID id, AllowanceTypeRequestDto request) {
        AllowanceType entity = findById(id);
        entity.setName(request.getName());
        entity.setIsTaxable(request.getIsTaxable());
        entity.setIsInsuranceBased(request.getIsInsuranceBased());
        return AllowanceTypeResponseDto.fromEntity(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(AllowanceType.class, id);
        }
        repository.deleteById(id);
    }

    private Specification<AllowanceType> buildSpec(AllowanceTypeFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter != null) {
                if (filter.getName() != null && !filter.getName().trim().isEmpty()) {
                    predicates = cb.and(predicates,
                            cb.like(cb.lower(root.get("name")), "%" + filter.getName().trim().toLowerCase() + "%"));
                }
                if (filter.getIsTaxable() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isTaxable"), filter.getIsTaxable()));
                }
                if (filter.getIsInsuranceBased() != null) {
                    predicates = cb.and(predicates,
                            cb.equal(root.get("isInsuranceBased"), filter.getIsInsuranceBased()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
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