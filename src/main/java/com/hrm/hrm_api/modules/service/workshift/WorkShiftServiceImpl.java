package com.hrm.hrm_api.modules.service.workshift;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.common.security.SecurityUtil;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.base.SelectOptionDto;
import com.hrm.hrm_api.modules.dto.request.WorkShiftSearchRequest;
import com.hrm.hrm_api.modules.dto.workshift.WorkShiftFilter;
import com.hrm.hrm_api.modules.entity.WorkShift;
import com.hrm.hrm_api.modules.repository.WorkShiftRepository;
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkShiftServiceImpl implements WorkShiftService {

    private final WorkShiftRepository repository;
    private final ActionLogUtil actionLogUtil;
    private final SecurityUtil securityUtil;

    public WorkShiftServiceImpl(
            WorkShiftRepository repository,
            ActionLogUtil actionLogUtil,
            SecurityUtil securityUtil) {
        this.repository = repository;
        this.actionLogUtil = actionLogUtil;
        this.securityUtil = securityUtil;
    }

    @Override
    public Page<WorkShift> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public WorkShift findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(WorkShift.class, id));
    }

    @Override
    @Transactional
    public WorkShift create(WorkShift entity) {
        WorkShift saved = repository.save(entity);
        
        actionLogUtil.logCreate(
                saved.getId(),
                "WORK_SHIFT",
                "Tạo ca làm việc: " + saved.getName(),
                securityUtil.getCurrentUsername());
        
        return saved;
    }

    @Override
    @Transactional
    public WorkShift update(UUID id, WorkShift entity) {
        WorkShift existing = findById(id);
        existing.setName(entity.getName());
        existing.setStartTime(entity.getStartTime());
        existing.setEndTime(entity.getEndTime());
        existing.setBreakStart(entity.getBreakStart());
        existing.setBreakEnd(entity.getBreakEnd());
        existing.setLateGraceMinutes(entity.getLateGraceMinutes());
        existing.setEarlyLeaveGraceMinutes(entity.getEarlyLeaveGraceMinutes());
        
        WorkShift updated = repository.save(existing);
        
        actionLogUtil.logUpdate(
                id,
                "WORK_SHIFT",
                "Cập nhật ca làm việc: " + updated.getName(),
                securityUtil.getCurrentUsername());
        
        return updated;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        WorkShift existing = findById(id);
        repository.deleteById(id);
        
        actionLogUtil.logDelete(
                id,
                "WORK_SHIFT",
                "Xóa ca làm việc: " + existing.getName(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Build specification for work shift search
     */
    private Specification<WorkShift> buildSpec(WorkShiftFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }
            
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            
            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"
                ));
            }
            
            if (filter.getIsDeleted() != null) {
                predicates.add(cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
            } else {
                // Default: exclude deleted records
                predicates.add(cb.equal(root.get("isDeleted"), false));
            }
            
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    @Override
    public PaginationResponse<WorkShift> search(WorkShiftSearchRequest request, Pageable pageable) {
        WorkShiftFilter filter = request != null ? request.getWhere() : null;
        Page<WorkShift> workShifts = repository.findAll(buildSpec(filter), pageable);
        
        return PaginationResponse.of(
                workShifts.getContent(),
                workShifts.getTotalElements(),
                workShifts.getNumber(),
                workShifts.getSize());
    }

    @Override
    public List<SelectOptionDto> getSelectOptions() {
        List<WorkShift> workShifts = repository.findAll((root, query, cb) -> 
                cb.equal(root.get("isDeleted"), false));
        
        return workShifts.stream()
                .map(shift -> new SelectOptionDto(shift.getId(), shift.getName()))
                .collect(Collectors.toList());
    }
}