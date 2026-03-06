package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PositionRepository extends JpaRepository<Position, UUID> {
    @Query(value = """
        SELECT * FROM positions p 
        WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER('%' || :name || '%'))
        AND (:isDeleted IS NULL OR p.is_deleted = :isDeleted)
        """, nativeQuery = true)
    Page<Position> findByFilters(
        @Param("name") String name,
        @Param("isDeleted") Boolean isDeleted,
        Pageable pageable
    );
}