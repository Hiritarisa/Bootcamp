package com.hiri.crediya.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PersonRequest {
    @NotBlank private String names;
    @NotBlank private String lastnames;
    @NotBlank private String document;
    private LocalDate birthdate;
    private String address;
    private String phone;
    @NotBlank @Email private String email;
    @NotNull @DecimalMin("0.0") @DecimalMax("15000000.0") private BigDecimal baseSalary;
}