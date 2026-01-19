package com.hrm.hrm_api.modules.service;

import com.hrm.hrm_api.modules.entity.Attendance;
import com.hrm.hrm_api.modules.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    public List<Attendance> getAttendancesByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);
    }

    // Method to record check-in via QR
    public Attendance recordCheckIn(Long employeeId, LocalDate date, LocalTime time) {
        Attendance attendance = new Attendance();
        // Assume employee is fetched, but for simplicity
        attendance.setEmployee(null); // Need to set
        attendance.setDate(date);
        attendance.setCheckInTime(time);
        attendance.setStatus("Present");
        return saveAttendance(attendance);
    }

    // Similarly for check-out
}
