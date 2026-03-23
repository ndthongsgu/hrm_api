package com.hrm.hrm_api.modules.service.position;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.PositionSearchRequest;
import com.hrm.hrm_api.modules.entity.Department;
import com.hrm.hrm_api.modules.entity.Position;
import com.hrm.hrm_api.modules.repository.DepartmentRepository;
import com.hrm.hrm_api.modules.repository.PositionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    public PositionServiceImpl(PositionRepository repository, DepartmentRepository departmentRepository) {
        this.positionRepository = repository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Page<Position> findAll(Pageable pageable) {
        return positionRepository.findAll(pageable);
    }

    @Override
    public Position findById(UUID id) {
        return positionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Position.class, id));
    }

    @Override
    @Transactional
    public Position create(Position entity) {
        return positionRepository.save(entity);
    }

    @Override
    @Transactional
    public Position update(UUID id, Position entity) {
        positionRepository.normalizeNullVersion(id);
        Position existing = findById(id);

        existing.setName(entity.getName());
        existing.setCode(entity.getCode());
        existing.setStandardResponsibilityAllowance(entity.getStandardResponsibilityAllowance());

        if (entity.getDepartment() == null || entity.getDepartment().getId() == null) {
            existing.setDepartment(null);
        } else {
            var departmentId = entity.getDepartment().getId();
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException(Department.class, departmentId));
            existing.setDepartment(department);
        }

        if (entity.getIsDeleted() != null) {
            existing.setIsDeleted(entity.getIsDeleted());
        }

        return positionRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!positionRepository.existsById(id)) {
            throw new ResourceNotFoundException(Position.class, id);
        }
        positionRepository.deleteById(id);
    }

    @Override
    public PaginationResponse<Position> search(PositionSearchRequest request) {
        if (request == null) {
            request = new PositionSearchRequest();
            request.setSkip(0);
            request.setTake(20);
        }

        int skip = request.getSkip() != null ? request.getSkip() : 0;
        int take = request.getTake() != null && request.getTake() > 0 ? request.getTake() : 20;

        if (skip < 0) skip = 0;
        if (take <= 0) take = 20;
        if (take > 100) take = 100;

        int pageNumber = skip / take;
        int pageSize = take;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "created_at"));

        var filter = request.getWhere();

        Page<Position> positions = positionRepository.findByFilters(
            filter != null ? filter.getName() : null,
            filter != null ? filter.getIsDeleted() : null,
            pageable
        );

        return PaginationResponse.of(
            positions.getContent(),
            positions.getTotalElements(),
            pageNumber,
            pageSize
        );
    }
}
