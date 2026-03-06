package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.ActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ActionLogRepository extends JpaRepository<ActionLog, UUID> {
    Page<ActionLog> findByFunctionTypeAndFunctionId(String functionType, String functionId, Pageable pageable);

    @Query("""
        SELECT a FROM ActionLog a 
        WHERE (:functionType IS NULL OR a.functionType = :functionType)
        AND (:functionId IS NULL OR a.functionId = :functionId)
        AND (:type IS NULL OR a.type = :type)
        AND (:createdBy IS NULL OR a.createdBy = :createdBy)
        ORDER BY a.createdAt DESC
        """)
    Page<ActionLog> findByFilters(
        @Param("functionType") String functionType,
        @Param("functionId") String functionId,
        @Param("type") String type,
        @Param("createdBy") String createdBy,
        Pageable pageable
    );
}
