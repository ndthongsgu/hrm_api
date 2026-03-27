package com.hrm.hrm_api.modules.service.dailyattendance;

import com.hrm.hrm_api.modules.dto.request.AttendanceCheckInRequest;
import com.hrm.hrm_api.modules.dto.response.AttendanceCheckInResponse;
import com.hrm.hrm_api.modules.entity.DailyAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DailyAttendanceService {
    Page<DailyAttendance> findAll(Pageable pageable);
    DailyAttendance findById(UUID id);
    DailyAttendance create(DailyAttendance entity);
    DailyAttendance update(UUID id, DailyAttendance entity);
    void delete(UUID id);
    
    /**
     * Check-in attendance with face verification and location validation
     * 
     * @param request Contains employee ID, image file, and GPS coordinates
     * @return CheckInResponse with attendance record and verification details
     */
    AttendanceCheckInResponse checkIn(AttendanceCheckInRequest request);

    /**
     * Check-out attendance - records check-out time for today's record
     * 
     * @param employeeId UUID of the employee
     * @param latitude GPS latitude
     * @param longitude GPS longitude
     * @return Updated DailyAttendance record with check-out time
     */
    DailyAttendance checkOut(UUID employeeId, Double latitude, Double longitude);

    /**
     * Get today's attendance record for an employee
     * 
     * @param employeeId UUID of the employee
     * @return Today's DailyAttendance record or null if not checked in yet
     */
    DailyAttendance getTodayAttendance(UUID employeeId);

    /**
     * Get today's shift information for an employee based on EmployeeShiftAssignment
     * This will return shift info even if no attendance record exists yet
     * 
     * @param employeeId UUID of the employee
     * @return Map with shift details (name, startTime, endTime, breakStart, breakEnd, checkInTime, checkOutTime, status)
     */
    java.util.Map<String, Object> getTodaysShiftInfo(UUID employeeId);
}