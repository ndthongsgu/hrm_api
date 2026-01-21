package com.hrm.hrm_api.modules.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "contract_allowances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractAllowance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "allowance_type_id")
    private AllowanceType allowanceType;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;
}