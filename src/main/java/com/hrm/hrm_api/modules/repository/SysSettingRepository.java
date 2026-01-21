package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.SysSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SysSettingRepository extends JpaRepository<SysSetting, UUID> {
}