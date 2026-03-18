package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "office_locations")
@Data
public class OfficeLocation extends BaseEntity {

    private String name; 

    private Double latitude;

    private Double longitude;

    private Double radius;

    private String address;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        APPROVED, PENDING
    }
}
