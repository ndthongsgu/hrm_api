package com.hrm.hrm_api.modules.service.dependent;

import com.hrm.hrm_api.modules.entity.Dependent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DependentService {
    Page<Dependent> findAll(Pageable pageable);
    Dependent findById(UUID id);
    Dependent create(Dependent entity);
    Dependent update(UUID id, Dependent entity);
    void delete(UUID id);
}
