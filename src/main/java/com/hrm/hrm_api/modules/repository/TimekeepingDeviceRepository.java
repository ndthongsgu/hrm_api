package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.TimekeepingDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimekeepingDeviceRepository extends JpaRepository<TimekeepingDevice, Long> {
}