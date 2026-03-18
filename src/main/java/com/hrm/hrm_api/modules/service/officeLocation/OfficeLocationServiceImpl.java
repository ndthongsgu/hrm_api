package com.hrm.hrm_api.modules.service.officeLocation;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.common.security.SecurityUtil;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.officeLocation.OfficeLocationFilter;
import com.hrm.hrm_api.modules.dto.request.OfficeLocationSearchRequest;
import com.hrm.hrm_api.modules.entity.OfficeLocation;
import com.hrm.hrm_api.modules.repository.OfficeLocationRepository;
import com.hrm.hrm_api.modules.util.ActionLogUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OfficeLocationServiceImpl implements OfficeLocationService {

    private final OfficeLocationRepository repository;
    private final ActionLogUtil actionLogUtil;
    private final SecurityUtil securityUtil;

    public OfficeLocationServiceImpl(OfficeLocationRepository repository, ActionLogUtil actionLogUtil, SecurityUtil securityUtil) {
        this.repository = repository;
        this.actionLogUtil = actionLogUtil;
        this.securityUtil = securityUtil;
    }

    @Override
    public OfficeLocation findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OfficeLocation.class, id));
    }

    @Override
    @Transactional
    public OfficeLocation create(OfficeLocation request) {
        OfficeLocation entity = new OfficeLocation();
        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        entity.setRadius(request.getRadius());

        OfficeLocation saved = repository.save(entity);

        try {
            actionLogUtil.logCreate(
                    saved.getId(),
                    "OFFICE_LOCATION",
                    "Tạo địa điểm văn phòng: " + saved.getName(),
                    securityUtil.getCurrentUsername());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi ghi log hành động: " + e.getMessage(), e);
        }

        return saved;
    }

    @Override
    @Transactional
    public OfficeLocation update(UUID id, OfficeLocation request) {
        OfficeLocation entity = findById(id);

        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        entity.setRadius(request.getRadius());

        OfficeLocation updated = repository.save(entity);

        actionLogUtil.logUpdate(
                updated.getId(),
                "OFFICE_LOCATION",
                "Cập nhật địa điểm văn phòng: " + updated.getName(),
                securityUtil.getCurrentUsername());

        return updated;
    }

    /**
     * Tìm kiếm địa điểm văn phòng với filters và phân trang
     */
    private Specification<OfficeLocation> buildSpec(OfficeLocationFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter != null) {
                if (filter.getName() != null && !filter.getName().isEmpty()) {
                    predicates = cb.and(predicates,
                            cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
                }
                if (filter.getAddress() != null && !filter.getAddress().isEmpty()) {
                    predicates = cb.and(predicates,
                            cb.like(cb.lower(root.get("address")), "%" + filter.getAddress().toLowerCase() + "%"));
                }
                if (filter.getStatus() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("status"), filter.getStatus()));
                }
                if (filter.getIsDeleted() != null) {
                    predicates = cb.and(predicates, cb.equal(root.get("isDeleted"), filter.getIsDeleted()));
                } else {
                    // Default to not deleted
                    predicates = cb.and(predicates,
                            cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
                }
            } else {
                // Default to not deleted
                predicates = cb.and(predicates,
                        cb.or(cb.isNull(root.get("isDeleted")), cb.isFalse(root.get("isDeleted"))));
            }

            return predicates;
        };
    }

    @Override
    public PaginationResponse<OfficeLocation> search(OfficeLocationSearchRequest request, Pageable pageable) {
        var filter = request != null ? request.getWhere() : null;

        Page<OfficeLocation> page = repository.findAll(buildSpec(filter), pageable);

        return PaginationResponse.of(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize());
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        OfficeLocation location = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OfficeLocation.class, id));
        location.setIsDeleted(true);
        repository.save(location);

        actionLogUtil.logDeactivate(
                id,
                "OFFICE_LOCATION",
                "Ngừng hoạt động địa điểm văn phòng: " + location.getName(),
                securityUtil.getCurrentUsername());
    }

    @Override
    @Transactional
    public void activate(UUID id) {
        OfficeLocation location = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OfficeLocation.class, id));
        location.setIsDeleted(false);
        repository.save(location);

        actionLogUtil.logActivate(
                id,
                "OFFICE_LOCATION",
                "Kích hoạt lại địa điểm văn phòng: " + location.getName(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Duyệt địa điểm văn phòng
     */
    @Override
    @Transactional
    public void approve(UUID id) {
        OfficeLocation location = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OfficeLocation.class, id));

        location.setStatus(OfficeLocation.Status.APPROVED);
        repository.save(location);

        actionLogUtil.logUpdate(
                id,
                "OFFICE_LOCATION",
                "Duyệt địa điểm văn phòng: " + location.getName(),
                securityUtil.getCurrentUsername());
    }

    /**
     * Hủy duyệt địa điểm văn phòng
     */
    @Override
    @Transactional
    public void unApprove(UUID id) {
        OfficeLocation location = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(OfficeLocation.class, id));

        location.setStatus(OfficeLocation.Status.PENDING);
        repository.save(location);

        actionLogUtil.logCancel(
                id,
                "OFFICE_LOCATION",
                "Hủy duyệt địa điểm văn phòng: " + location.getName(),
                securityUtil.getCurrentUsername());
    }
}
