package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
}