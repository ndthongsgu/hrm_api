package com.hrm.hrm_api.modules.service.taxbracket;

import com.hrm.hrm_api.modules.entity.TaxBracket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaxBracketService {
    Page<TaxBracket> findAll(Pageable pageable);
    TaxBracket findById(UUID id);
    TaxBracket create(TaxBracket entity);
    TaxBracket update(UUID id, TaxBracket entity);
    void delete(UUID id);
}