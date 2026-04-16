package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.dto.base.BaseResponse;
import com.hrm.hrm_api.modules.dto.base.PaginationResponse;
import com.hrm.hrm_api.modules.dto.request.AttendanceCheckInRequest;
import com.hrm.hrm_api.modules.dto.request.DailyAttendanceSearchRequest;
import com.hrm.hrm_api.modules.dto.request.DailyAttendanceCreateRequest;
import com.hrm.hrm_api.modules.dto.response.AttendanceCheckInResponse;
import com.hrm.hrm_api.modules.dto.request.DailyAttendanceUpdateRequest;
import com.hrm.hrm_api.modules.entity.DailyAttendance;
import com.hrm.hrm_api.modules.service.dailyattendance.DailyAttendanceService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/daily-attendances")
public class DailyAttendanceController {

    private final DailyAttendanceService service;

    public DailyAttendanceController(DailyAttendanceService service) {
        this.service = service;
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<PaginationResponse<DailyAttendance>>> search(@RequestBody(required = false) DailyAttendanceSearchRequest request, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PaginationResponse<DailyAttendance> response = service.search(request, pageable);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<DailyAttendance>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<DailyAttendance>> create(@Valid @RequestBody DailyAttendanceCreateRequest payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.create(payload)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<DailyAttendance>> update(@PathVariable UUID id, @Valid @RequestBody DailyAttendanceUpdateRequest payload) {
        return ResponseEntity.ok(BaseResponse.ok(service.update(id, payload)));
    }

    @DeleteMapping("/{id}/deactivate")
    public ResponseEntity<BaseResponse<String>> deactivate(@PathVariable UUID id) {
        service.deactivate(id);
        return ResponseEntity.ok(BaseResponse.ok("Ngừng hoạt động chấm công thành công"));
    }

    /**
     * Kích hoạt lại chấm công (Soft delete recovery)
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<BaseResponse<String>> activate(@PathVariable UUID id) {
        service.activate(id);
        return ResponseEntity.ok(BaseResponse.ok("Kích hoạt lại chấm công thành công"));
    }

    /**
     * Get today's attendance record for an employee
     * 
     * @param employeeId UUID of the employee
     * @return Today's DailyAttendance record or null if not found
     */
    @GetMapping("/today/{employeeId}")
    public ResponseEntity<BaseResponse<DailyAttendance>> getTodayAttendance(@PathVariable String employeeId) {
        DailyAttendance attendance = service.getTodayAttendance(java.util.UUID.fromString(employeeId));
        return ResponseEntity.ok(BaseResponse.ok(attendance));
    }

    /**
     * Get today's shift for an employee
     * Retrieves shift information from employee shift assignment and today's attendance status
     * Returns shift info even if no attendance record exists yet
     * 
     * @param employeeId UUID of the employee
     * @return Shift info with name, start time, end time, plus checkInTime and checkOutTime if available
     */
    @GetMapping("/shift/today/{employeeId}")
    public ResponseEntity<BaseResponse<HashMap<String, Object>>> getTodaysShift(@PathVariable String employeeId) {
        try {
            java.util.UUID uuid = java.util.UUID.fromString(employeeId);
            // Get shift info from service (includes shift assignment + attendance info)
            HashMap<String, Object> shiftInfo = (HashMap<String, Object>) service.getTodaysShiftInfo(uuid);
            
            System.out.println("Shift info for employee " + employeeId + ": " + shiftInfo);
            return ResponseEntity.ok(BaseResponse.ok(shiftInfo));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid employee ID format: " + employeeId, e);
        }
    }

    /**
     * Get monthly attendance data for an employee
     * 
     * @param employeeId UUID of the employee
     * @param year Year (optional, defaults to current year)
     * @param month Month 1-12 (optional, defaults to current month)
     * @return Map containing attendance summary and daily records for the month
     */
    @GetMapping("/monthly/{employeeId}")
    public ResponseEntity<BaseResponse<HashMap<String, Object>>> getMonthlyAttendance(
            @PathVariable String employeeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        try {
            java.util.UUID uuid = java.util.UUID.fromString(employeeId);
            
            // Use current date if year/month not provided
            if (year == null) {
                year = LocalDate.now().getYear();
            }
            if (month == null) {
                month = LocalDate.now().getMonthValue();
            }
            
            HashMap<String, Object> monthlyData = (HashMap<String, Object>) service.getMonthlyAttendance(uuid, year, month);
            return ResponseEntity.ok(BaseResponse.ok(monthlyData));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid employee ID format: " + employeeId, e);
        }
    }

    /**
     * Check-in endpoint for mobile app
     * 
     * Receives:
     * - employee_id: ID of the employee (form parameter)
     * - file: Image file for face verification (multipart file)
     * - latitude: GPS latitude (form parameter)
     * - longitude: GPS longitude (form parameter)
     * - deviceId: Optional device identifier (form parameter)
     * - notes: Optional additional notes (form parameter)
     */
    @PostMapping("/check-in")
    public ResponseEntity<BaseResponse<AttendanceCheckInResponse>> checkIn(
            @RequestParam String employeeId,
            @RequestPart(name = "file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String notes) {
        
        AttendanceCheckInRequest request = new AttendanceCheckInRequest();
        request.setEmployeeId(employeeId);
        request.setImageFile(file);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setDeviceId(deviceId);
        request.setNotes(notes);

        AttendanceCheckInResponse response = service.checkIn(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    /**
     * Check-out endpoint for mobile app
     * 
     * Receives:
     * - employeeId: ID of the employee
     * - latitude: GPS latitude (form parameter)
     * - longitude: GPS longitude (form parameter)
     * - deviceId: Optional device identifier (form parameter)
     * - notes: Optional additional notes (form parameter)
     */
    @PostMapping("/check-out")
    public ResponseEntity<BaseResponse<DailyAttendance>> checkOut(
            @RequestParam String employeeId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String notes) {
        
        DailyAttendance attendance = service.checkOut(java.util.UUID.fromString(employeeId), latitude, longitude);
        return ResponseEntity.ok(BaseResponse.ok(attendance));
    }
}