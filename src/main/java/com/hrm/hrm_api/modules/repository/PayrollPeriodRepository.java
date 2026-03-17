package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.PayrollPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PayrollPeriodRepository extends JpaRepository<PayrollPeriod, UUID>, JpaSpecificationExecutor<PayrollPeriod> {
    List<PayrollPeriod> findAllByIsDeletedFalseOrderByCreatedAtDesc();
    List<PayrollPeriod> findAllByIsDeletedAndStatusOrderByCreatedAtDesc(Boolean isDeleted, PayrollPeriod.Status status);
    
    Optional<PayrollPeriod> findByIdAndStatus(UUID id, PayrollPeriod.Status status);
    
    @Query("SELECT p FROM PayrollPeriod p WHERE p.id = :id AND p.status = :status AND p.isDeleted = :isDeleted")
    Optional<PayrollPeriod> findByIdAndStatusAndIsDeleted(
        @Param("id") UUID id,
        @Param("status") PayrollPeriod.Status status,
        @Param("isDeleted") Boolean isDeleted
    );
}