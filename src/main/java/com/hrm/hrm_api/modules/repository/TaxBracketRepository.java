package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.TaxBracket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaxBracketRepository extends JpaRepository<TaxBracket, UUID> {
    
    @Query("SELECT tb FROM TaxBracket tb WHERE tb.isActive = :isActive ORDER BY tb.minIncome ASC")
    List<TaxBracket> findByIsActive(@Param("isActive") Boolean isActive);
}