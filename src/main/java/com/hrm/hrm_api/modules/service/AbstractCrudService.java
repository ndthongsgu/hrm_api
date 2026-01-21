package com.hrm.hrm_api.modules.service;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public abstract class AbstractCrudService<T> implements CrudService<T> {

    protected final JpaRepository<T, UUID> repository;
    private final Class<T> entityClass;

    protected AbstractCrudService(JpaRepository<T, UUID> repository, Class<T> entityClass) {
        this.repository = repository;
        this.entityClass = entityClass;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public T findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(entityClass, id));
    }

    @Override
    @Transactional
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public T update(UUID id, T entity) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(entityClass, id);
        }
        try {
            java.lang.reflect.Field f = entity.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // If no id field, proceed and let JPA handle
        }
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(entityClass, id);
        }
        repository.deleteById(id);
    }
}
