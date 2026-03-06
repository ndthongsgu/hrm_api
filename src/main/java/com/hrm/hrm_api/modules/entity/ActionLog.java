package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "action_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ActionLog extends BaseEntity {

    @Column(nullable = false)
    private String functionId;

    @Column(nullable = false)
    private String functionType;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String createdByName;

    @Column
    private String employeeCode;
}
