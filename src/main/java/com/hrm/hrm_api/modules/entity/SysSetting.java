package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_name", unique = true, nullable = false)
    private String key;

    @Column(precision = 15, scale = 2)
    private BigDecimal value;

    private String description;

    private LocalDate effectiveDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
}