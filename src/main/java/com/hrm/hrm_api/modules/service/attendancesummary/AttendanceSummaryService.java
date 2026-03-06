package com.hrm.hrm_api.modules.service.attendancesummary;

import com.hrm.hrm_api.modules.entity.AttendanceSummary;
import com.hrm.hrm_api.modules.service.CrudService;

import java.util.UUID;

public interface AttendanceSummaryService extends CrudService<AttendanceSummary> {
    void approveAttendance(UUID attendanceSummaryId);
}