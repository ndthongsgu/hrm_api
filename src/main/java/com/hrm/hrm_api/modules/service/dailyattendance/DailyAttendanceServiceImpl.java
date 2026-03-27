package com.hrm.hrm_api.modules.service.dailyattendance;

import com.hrm.hrm_api.common.exception.ResourceNotFoundException;
import com.hrm.hrm_api.modules.client.FaceVerificationClient;
import com.hrm.hrm_api.modules.dto.request.AttendanceCheckInRequest;
import com.hrm.hrm_api.modules.dto.response.AttendanceCheckInResponse;
import com.hrm.hrm_api.modules.entity.DailyAttendance;
import com.hrm.hrm_api.modules.entity.Employee;
import com.hrm.hrm_api.modules.entity.EmployeeShiftAssignment;
import com.hrm.hrm_api.modules.entity.OfficeLocation;
import com.hrm.hrm_api.modules.repository.DailyAttendanceRepository;
import com.hrm.hrm_api.modules.repository.EmployeeRepository;
import com.hrm.hrm_api.modules.repository.EmployeeShiftAssignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
public class DailyAttendanceServiceImpl implements DailyAttendanceService {

    private final DailyAttendanceRepository repository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeShiftAssignmentRepository employeeShiftAssignmentRepository;
    private final FaceVerificationClient faceVerificationClient;

    public DailyAttendanceServiceImpl(
        DailyAttendanceRepository repository,
        EmployeeRepository employeeRepository,
        EmployeeShiftAssignmentRepository employeeShiftAssignmentRepository,
        FaceVerificationClient faceVerificationClient
    ) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.employeeShiftAssignmentRepository = employeeShiftAssignmentRepository;
        this.faceVerificationClient = faceVerificationClient;
    }

    @Override
    public Page<DailyAttendance> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public DailyAttendance findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(DailyAttendance.class, id));
    }

    @Override
    @Transactional
    public DailyAttendance create(DailyAttendance entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public DailyAttendance update(UUID id, DailyAttendance entity) {
        findById(id);
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(DailyAttendance.class, id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public AttendanceCheckInResponse checkIn(AttendanceCheckInRequest request) {
        AttendanceCheckInResponse.AttendanceCheckInResponseBuilder responseBuilder = AttendanceCheckInResponse.builder();
        
        try {
            // Step 1: Verify employee exists
            UUID employeeId = UUID.fromString(request.getEmployeeId());
            Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("Employee not found: {}", request.getEmployeeId());
                    return new ResourceNotFoundException(Employee.class, employeeId);
                });

            responseBuilder
                .employeeId(request.getEmployeeId())
                .employeeName(employee.getFullName());

            // Step 2: Verify face with Python API
            FaceVerificationClient.VerifyFaceResponse faceVerification = faceVerificationClient.verifyFace(
                request.getEmployeeId(),
                request.getImageFile()
            );

            log.info("Face verification result for employee {}: {}", request.getEmployeeId(), faceVerification.getStatus());
            
            responseBuilder
                .faceVerified("success".equalsIgnoreCase(faceVerification.getStatus()))
                .faceVerificationMessage(faceVerification.getMessage());

            // If face verification failed, return early
            if (!"success".equalsIgnoreCase(faceVerification.getStatus())) {
                return responseBuilder
                    .success(false)
                    .checkInResult("FACE_VERIFICATION_FAILED")
                    .message("Xác minh khuôn mặt thất bại: " + faceVerification.getMessage())
                    .build();
            }

            // Step 3: Verify location
            OfficeLocation officeLocation = employee.getOfficeLocation();
            if (officeLocation == null) {
                return responseBuilder
                    .success(false)
                    .locationVerified(false)
                    .checkInResult("LOCATION_NOT_CONFIGURED")
                    .message("Vị trí văn phòng chưa được cấu hình cho nhân viên này")
                    .build();
            }

            responseBuilder.officeLocationName(officeLocation.getName());

            // Calculate distance between user location and office location
            Double distance = calculateDistance(
                request.getLatitude(),
                request.getLongitude(),
                officeLocation.getLatitude(),
                officeLocation.getLongitude()
            );

            responseBuilder.distanceToOffice(distance);

            // Check if user is within the office location radius
            boolean isWithinRadius = distance <= officeLocation.getRadius();
            responseBuilder.locationVerified(isWithinRadius);

            if (!isWithinRadius) {
                log.warn("Employee {} checked in outside office location. Distance: {} meters, Radius: {} meters",
                    request.getEmployeeId(), distance, officeLocation.getRadius());
                return responseBuilder
                    .success(false)
                    .checkInResult("LOCATION_NOT_VERIFIED")
                    .message(String.format("Vị trí không hợp lệ. Khoảng cách: %.2f mét, bán kính cho phép: %.2f mét",
                        distance, officeLocation.getRadius()))
                    .build();
            }

            // Step 4: Create attendance record with employee's assigned shift
            DailyAttendance attendance = new DailyAttendance();
            attendance.setEmployee(employee);
            attendance.setDate(LocalDate.now());
            attendance.setCheckInTime(LocalDateTime.now());
            attendance.setStatus(DailyAttendance.Status.OK);
            
            // Get and set the employee's shift assignment for today
            EmployeeShiftAssignment shiftAssignment = employeeShiftAssignmentRepository
                .findActiveAssignmentOnDate(employeeId, LocalDate.now())
                .orElse(null);
            
            if (shiftAssignment != null) {
                attendance.setShift(shiftAssignment.getShift());
                
                // Calculate late_minutes if check-in is after shift start time
                LocalTime shiftStartTime = shiftAssignment.getShift().getStartTime();
                LocalDateTime shiftStartDateTime = LocalDateTime.of(LocalDate.now(), shiftStartTime);
                
                if (attendance.getCheckInTime().isAfter(shiftStartDateTime)) {
                    long lateMinutes = ChronoUnit.MINUTES.between(shiftStartDateTime, attendance.getCheckInTime());
                    attendance.setLateMinutes((int) lateMinutes);
                    log.info("Employee {} checked in late by {} minutes", request.getEmployeeId(), lateMinutes);
                } else {
                    attendance.setLateMinutes(0);
                }
            }
            
            if (request.getNotes() != null) {
                // Optional: store device info in notes if needed
            }

            DailyAttendance savedAttendance = repository.save(attendance);            // Ensure data is flushed to database immediately
            repository.flush();
            return responseBuilder
                .success(true)
                .attendanceId(savedAttendance.getId())
                .checkInTime(savedAttendance.getCheckInTime())
                .status("OK")
                .checkInResult("SUCCESS")
                .message("Chấm công thành công")
                .build();

        } catch (IllegalArgumentException e) {
            log.error("Invalid employee ID format: {}", request.getEmployeeId(), e);
            return responseBuilder
                .success(false)
                .checkInResult("OTHER_ERROR")
                .message("Định dạng ID nhân viên không hợp lệ")
                .build();
        } catch (Exception e) {
            log.error("Error during check-in process", e);
            return responseBuilder
                .success(false)
                .checkInResult("OTHER_ERROR")
                .message("Lỗi trong quá trình chấm công: " + e.getMessage())
                .build();
        }
    }

    /**
     * Calculate distance between two geographic points using Haversine formula
     * 
     * @param lat1 Latitude of point 1 (degrees)
     * @param lon1 Longitude of point 1 (degrees)
     * @param lat2 Latitude of point 2 (degrees)
     * @param lon2 Longitude of point 2 (degrees)
     * @return Distance in meters
     */
    private Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int EARTH_RADIUS = 6371; // Earth radius in kilometers

        Double dLat = Math.toRadians(lat2 - lat1);
        Double dLon = Math.toRadians(lon2 - lon1);

        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distance = EARTH_RADIUS * c; // distance in kilometer

        return distance * 1000; // convert to meters
    }

    @Override
    public DailyAttendance getTodayAttendance(UUID employeeId) {
        LocalDate today = LocalDate.now();
        
        // Get the employee's shift assignment for today
        EmployeeShiftAssignment shiftAssignment = employeeShiftAssignmentRepository
            .findActiveAssignmentOnDate(employeeId, today)
            .orElse(null);
        
        // If no active shift assignment, return null
        if (shiftAssignment == null) {
            log.debug("No active shift assignment found for employee {} on {}", employeeId, today);
            return null;
        }
        
        // Search for attendance by employeeId, shiftId, and date
        DailyAttendance attendance = repository.findByEmployeeIdAndShiftIdAndDate(
            employeeId, 
            shiftAssignment.getShift().getId(), 
            today
        );
        
        if (attendance == null) {
            log.debug("No attendance record found for employee {} on shift {} on date {}", 
                employeeId, shiftAssignment.getShift().getId(), today);
        } else {
            log.debug("Found attendance record for employee {} - checkIn: {}, checkout: {}", 
                employeeId, attendance.getCheckInTime(), attendance.getCheckOutTime());
        }
        
        return attendance;
    }

    @Override
    @Transactional
    public DailyAttendance checkOut(UUID employeeId, Double latitude, Double longitude) {
        // Use getTodayAttendance which now properly searches by employeeId, shiftId, and date
        DailyAttendance attendance = getTodayAttendance(employeeId);
        
        if (attendance == null) {
            throw new RuntimeException("Không tìm thấy bản ghi chấm công hôm nay. Vui lòng chấm công (check-in) trước.");
        }
        
        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Bạn đã kết thúc chấm công rồi.");
        }
        
        // Set check-out time
        LocalDateTime checkOutTime = LocalDateTime.now();
        attendance.setCheckOutTime(checkOutTime);
        
        // Update status to OK (both checked-in and checked-out)
        attendance.setStatus(DailyAttendance.Status.OK);
        
        // Calculate early_minutes and work_hours
        if (attendance.getShift() != null) {
            LocalTime shiftEndTime = attendance.getShift().getEndTime();
            LocalDateTime shiftEndDateTime = LocalDateTime.of(LocalDate.now(), shiftEndTime);
            
            // Calculate early_minutes if check-out is before shift end time
            if (checkOutTime.isBefore(shiftEndDateTime)) {
                long earlyMinutes = ChronoUnit.MINUTES.between(checkOutTime, shiftEndDateTime);
                attendance.setEarlyMinutes((int) earlyMinutes);
                log.info("Employee {} checked out early by {} minutes", employeeId, earlyMinutes);
            } else {
                attendance.setEarlyMinutes(0);
            }
        }
        
        // Calculate work_hours (duration from check-in to check-out)
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            long minutesWorked = ChronoUnit.MINUTES.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
            float hoursWorked = minutesWorked / 60.0f;
            attendance.setWorkingHours(hoursWorked);
            log.info("Employee {} work hours: {}", employeeId, hoursWorked);
        }
        
        return repository.save(attendance);
    }

    @Override
    public java.util.Map<String, Object> getTodaysShiftInfo(UUID employeeId) {
        LocalDate today = LocalDate.now();
        java.util.HashMap<String, Object> shiftInfo = new java.util.HashMap<>();
        
        // Get employee's shift assignment for today
        java.util.Optional<EmployeeShiftAssignment> shiftAssignment = employeeShiftAssignmentRepository
            .findActiveAssignmentOnDate(employeeId, today);
        
        // Get shift info from assignment (regardless of attendance status)
        if (shiftAssignment.isPresent()) {
            com.hrm.hrm_api.modules.entity.WorkShift shift = shiftAssignment.get().getShift();
            shiftInfo.put("name", shift.getName());
            shiftInfo.put("startTime", shift.getStartTime());
            shiftInfo.put("endTime", shift.getEndTime());
            shiftInfo.put("breakStart", shift.getBreakStart());
            shiftInfo.put("breakEnd", shift.getBreakEnd());
            log.debug("Found shift assignment for employee {} on {}: {}", employeeId, today, shift.getName());
        } else {
            // No shift assignment for today
            shiftInfo.put("name", null);
            shiftInfo.put("startTime", null);
            shiftInfo.put("endTime", null);
            shiftInfo.put("breakStart", null);
            shiftInfo.put("breakEnd", null);
            log.debug("No shift assignment found for employee {} on {}", employeeId, today);
        }
        
        // Get attendance record if it exists (for check-in/check-out times)
        DailyAttendance attendance = getTodayAttendance(employeeId);
        if (attendance != null) {
            shiftInfo.put("checkInTime", attendance.getCheckInTime());
            shiftInfo.put("checkOutTime", attendance.getCheckOutTime());
            shiftInfo.put("status", attendance.getStatus());
            shiftInfo.put("date", attendance.getDate());
            log.debug("Found attendance record for employee {} on {}", employeeId, today);
        } else {
            shiftInfo.put("checkInTime", null);
            shiftInfo.put("checkOutTime", null);
            shiftInfo.put("status", null);
            shiftInfo.put("date", today);
            log.debug("No attendance record found for employee {} on {}", employeeId, today);
        }
        
        return shiftInfo;
    }
}