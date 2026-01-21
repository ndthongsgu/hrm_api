package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkShiftRepository extends JpaRepository<WorkShift, UUID> {
}