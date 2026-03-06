package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.TaxBracket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaxBracketRepository extends JpaRepository<TaxBracket, UUID> {
	List<TaxBracket> findByIsActiveTrueOrderByMinIncomeAsc();

	List<TaxBracket> findByIsActiveTrueAndEffectiveDateLessThanEqualOrderByMinIncomeAsc(LocalDate effectiveDate);
}