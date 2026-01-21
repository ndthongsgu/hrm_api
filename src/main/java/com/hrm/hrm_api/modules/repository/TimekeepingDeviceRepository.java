package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.TimekeepingDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import java.util.UUID;

public interface TimekeepingDeviceRepository extends JpaRepository<TimekeepingDevice, UUID> {
}