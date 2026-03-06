package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.Role;
import com.hrm.hrm_api.modules.dto.request.RoleSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query(value = """
        SELECT * FROM roles r
        WHERE (:code IS NULL OR LOWER(r.code) LIKE LOWER(CONCAT('%', :code, '%')))
        AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:isActive IS NULL OR r.is_active = :isActive)
        """, nativeQuery = true)
    Page<Role> findByFilters(
            @Param("code") String code,
            @Param("name") String name,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}
