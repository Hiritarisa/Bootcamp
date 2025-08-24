package com.hiri.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table("persons")
public class PersonEntity {
    @Id private Long id;
    private String names;
    private String lastnames;
    private LocalDate birthdate;
    private String address;
    private String phone;
    private String email;
    private BigDecimal baseSalary;
}