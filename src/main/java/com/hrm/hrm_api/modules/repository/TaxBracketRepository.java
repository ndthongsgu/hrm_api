package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.TaxBracket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaxBracketRepository extends JpaRepository<TaxBracket, UUID> {
}