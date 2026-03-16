package com.hrm.hrm_api.modules.service.syssetting;

import com.hrm.hrm_api.modules.entity.SysSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SysSettingService {
    Page<SysSetting> findAll(Pageable pageable);
    SysSetting findById(UUID id);
    SysSetting create(SysSetting entity);
    SysSetting update(UUID id, SysSetting entity);
    void delete(UUID id);
}