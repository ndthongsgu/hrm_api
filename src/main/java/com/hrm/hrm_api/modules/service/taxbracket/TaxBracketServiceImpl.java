package com.hrm.hrm_api.modules.service.taxbracket;

import com.hrm.hrm_api.modules.entity.TaxBracket;
import com.hrm.hrm_api.modules.repository.TaxBracketRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class TaxBracketServiceImpl extends AbstractCrudService<TaxBracket> implements TaxBracketService {

    public TaxBracketServiceImpl(TaxBracketRepository repository) {
        super(repository, TaxBracket.class);
    }
}