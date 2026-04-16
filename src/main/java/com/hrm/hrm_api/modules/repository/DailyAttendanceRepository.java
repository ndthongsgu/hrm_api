package com.hrm.hrm_api.modules.repository;

import com.hrm.hrm_api.modules.entity.DailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DailyAttendanceRepository extends JpaRepository<DailyAttendance, UUID>, JpaSpecificationExecutor<DailyAttendance> {
    
    /**
     * Đếm số ngày làm việc thực tế (status = 'OK', 'LATE') của nhân viên trong kỳ lương
     */
    @Query(value = "SELECT COUNT(*) FROM daily_attendances " +
            "WHERE employee_id = :employeeId " +
            "AND date BETWEEN :startDate AND :endDate " +
            "AND status IN ('OK', 'LATE')", nativeQuery = true)
    Long countActualWorkingDays(@Param("employeeId") UUID employeeId, 
                                @Param("startDate") LocalDate startDate, 
                                @Param("endDate") LocalDate endDate);

    /**
     * Đếm số ngày nghỉ có lương (status = 'LEAVE') của nhân viên trong kỳ lương
     */
    @Query(value = "SELECT COUNT(*) FROM daily_attendances " +
            "WHERE employee_id = :employeeId " +
            "AND date BETWEEN :startDate AND :endDate " +
            "AND status = 'LEAVE'", nativeQuery = true)
    Long countPaidLeaveDays(@Param("employeeId") UUID employeeId, 
                            @Param("startDate") LocalDate startDate, 
                            @Param("endDate") LocalDate endDate);

    /**
     * Đếm số ngày vắng không lương (status = 'ABSENT') của nhân viên trong kỳ lương
     */
    @Query(value = "SELECT COUNT(*) FROM daily_attendances " +
            "WHERE employee_id = :employeeId " +
            "AND date BETWEEN :startDate AND :endDate " +
            "AND status = 'ABSENT'", nativeQuery = true)
    Long countUnpaidLeaveDays(@Param("employeeId") UUID employeeId, 
                              @Param("startDate") LocalDate startDate, 
                              @Param("endDate") LocalDate endDate);

    /**
     * Tính tổng giờ làm thêm (nếu working_hours > expected_hours) của nhân viên trong kỳ lương
     */
    @Query(value = "SELECT COALESCE(SUM(CASE " +
            "WHEN da.working_hours > EXTRACT(EPOCH FROM (ws.end_time - ws.start_time))/3600 " +
            "THEN (da.working_hours - EXTRACT(EPOCH FROM (ws.end_time - ws.start_time))/3600) " +
            "ELSE 0 END), 0) " +
            "FROM daily_attendances da " +
            "LEFT JOIN work_shifts ws ON da.shift_id = ws.id " +
            "WHERE da.employee_id = :employeeId " +
            "AND da.date BETWEEN :startDate AND :endDate " +
            "AND da.status = 'OK'", nativeQuery = true)
    Double sumOvertimeHours(@Param("employeeId") UUID employeeId, 
                           @Param("startDate") LocalDate startDate, 
                           @Param("endDate") LocalDate endDate);

    /**
     * Find today's attendance record for an employee
     */
    DailyAttendance findByEmployeeIdAndDate(UUID employeeId, LocalDate date);
    
    /**
     * Find attendance record by employeeId, shiftId, and date
     * @param employeeId Employee ID
     * @param shiftId Shift ID
     * @param date Attendance date
     * @return DailyAttendance record if found
     */
    DailyAttendance findByEmployeeIdAndShiftIdAndDate(UUID employeeId, UUID shiftId, LocalDate date);

    List<DailyAttendance> findByEmployeeIdAndDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);
}