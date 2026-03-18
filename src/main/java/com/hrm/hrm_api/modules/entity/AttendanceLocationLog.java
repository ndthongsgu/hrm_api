package com.hrm.hrm_api.modules.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "attendance_location_logs")
@Data
public class AttendanceLocationLog extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Vị trí user gửi lên
    private Double latitude;

    private Double longitude;

    // Địa chỉ (optional)
    private String address;

    // Thời điểm check-in / check-out
    private LocalDateTime checkTime;

    // Loại: CHECK_IN / CHECK_OUT
    @Enumerated(EnumType.STRING)
    private AttendanceType type;

    // Khoảng cách tới vị trí hợp lệ (mét)
    private Double distance;

    // Có hợp lệ không
    private Boolean isValid;

    // Tham chiếu vị trí công ty
    @ManyToOne
    @JoinColumn(name = "office_location_id")
    private OfficeLocation officeLocation;

    // Tham chiếu bảng DailyAttendance
    @ManyToOne
    @JoinColumn(name = "daily_attendance_id")
    private DailyAttendance dailyAttendance;

    public enum AttendanceType {
        CHECK_IN,
        CHECK_OUT
    }
}
