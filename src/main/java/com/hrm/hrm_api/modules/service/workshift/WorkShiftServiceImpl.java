package com.hrm.hrm_api.modules.service.workshift;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.WorkShift;
import com.hrm.hrm_api.modules.repository.WorkShiftRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WorkShiftServiceImpl implements WorkShiftService {

    private final WorkShiftRepository repository;

    public WorkShiftServiceImpl(WorkShiftRepository repository) {
        this.repository = repository;
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
        return repository.save(entity);
    }

    @Override
    @Transactional
    public WorkShift update(UUID id, WorkShift entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(WorkShift.class, id);
        }
        repository.deleteById(id);
    }
}