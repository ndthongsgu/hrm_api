package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.OfficeLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OfficeLocationRepository extends JpaRepository<OfficeLocation, UUID>, JpaSpecificationExecutor<OfficeLocation> {
}
