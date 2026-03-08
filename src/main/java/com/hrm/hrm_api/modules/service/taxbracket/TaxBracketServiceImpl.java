package com.hrm.hrm_api.modules.service.taxbracket;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.entity.TaxBracket;
import com.hrm.hrm_api.modules.repository.TaxBracketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TaxBracketServiceImpl implements TaxBracketService {

    private final TaxBracketRepository repository;

    public TaxBracketServiceImpl(TaxBracketRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<TaxBracket> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public TaxBracket findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(TaxBracket.class, id));
    }

    @Override
    @Transactional
    public TaxBracket create(TaxBracket entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public TaxBracket update(UUID id, TaxBracket entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(TaxBracket.class, id);
        }
        repository.deleteById(id);
    }
}