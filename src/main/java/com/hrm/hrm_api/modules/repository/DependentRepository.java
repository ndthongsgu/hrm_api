package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Dependent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DependentRepository extends JpaRepository<Dependent, Long> {
}