package com.hrm.hrm_api.modules.service.attendancesummary;

import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import com.hrm.hrm_api.modules.repository.AttendanceSummaryRepository;
import com.hrm.hrm_api.modules.service.AbstractCrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AttendanceSummaryServiceImpl extends AbstractCrudService<AttendanceSummary> implements AttendanceSummaryService {

    private final AttendanceSummaryRepository repository;

    public AttendanceSummaryServiceImpl(AttendanceSummaryRepository repository) {
        super(repository, AttendanceSummary.class);
        this.repository = repository;
    }

    /**
     * Duyệt công - đóng băng công cho kỳ (Bước 2)
     */
    @Override
    @Transactional
    public void approveAttendance(UUID attendanceSummaryId) {
        AttendanceSummary summary = repository.findById(attendanceSummaryId)
                .orElseThrow(() -> new RuntimeException("AttendanceSummary not found"));
        
        summary.setIsApproved(true);
        repository.save(summary);
    }
}