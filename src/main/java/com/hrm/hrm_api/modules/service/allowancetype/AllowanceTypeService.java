package com.hrm.hrm_api.modules.service.allowancetype;

import com.hrm.hrm_api.modules.entity.AllowanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AllowanceTypeService {
    Page<AllowanceType> findAll(Pageable pageable);
    AllowanceType findById(UUID id);
    AllowanceType create(AllowanceType entity);
    AllowanceType update(UUID id, AllowanceType entity);
    void delete(UUID id);
}