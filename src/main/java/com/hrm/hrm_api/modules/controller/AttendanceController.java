package com.hrm.hrm_api.modules.controller;

import com.hrm.hrm_api.modules.entity.Attendance;
import com.hrm.hrm_api.modules.service.AttendanceService;
import com.hrm.hrm_api.modules.service.QrService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private QrService qrService;

    @GetMapping
    public List<Attendance> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }

    @GetMapping("/{id}")
    public Attendance getAttendanceById(@PathVariable Long id) {
        return attendanceService.getAttendanceById(id).orElse(null);
    }

    @PostMapping
    public Attendance createAttendance(@RequestBody Attendance attendance) {
        return attendanceService.saveAttendance(attendance);
    }

    @PutMapping("/{id}")
    public Attendance updateAttendance(@PathVariable Long id, @RequestBody Attendance attendance) {
        attendance.setId(id);
        return attendanceService.saveAttendance(attendance);
    }

    @DeleteMapping("/{id}")
    public void deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
    }

    // Endpoint for QR check-in
    @PostMapping("/checkin/{employeeId}")
    public Attendance checkIn(@PathVariable Long employeeId, @RequestParam LocalDate date, @RequestParam LocalTime time) {
        return attendanceService.recordCheckIn(employeeId, date, time);
    }

    @GetMapping("/qr/{employeeId}")
    public ResponseEntity<byte[]> generateQrCode(@PathVariable Long employeeId) throws WriterException, IOException {
        String qrText = "Employee Check-in: " + employeeId;
        byte[] qrCode = qrService.generateQrCode(qrText);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(headers).body(qrCode);
    }
}
