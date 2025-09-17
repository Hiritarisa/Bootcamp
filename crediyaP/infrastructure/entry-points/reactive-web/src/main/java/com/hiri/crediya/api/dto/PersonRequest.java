package com.hiri.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(description = "Required data to create or update a user")
public class PersonRequest {
    
    @Schema(description = "Unique user identifier")
    private UUID id;
    
    @Schema(description = "User first names", required = true)
    @NotBlank private String names;
    
    @Schema(description = "User last names", required = true)
    @NotBlank private String lastnames;
    
    @Schema(description = "User password", required = true)
    @NotNull @NotBlank private String password;
    
    @Schema(description = "User document number", required = true)
    @NotBlank private String document;
    
    @Schema(description = "User birth date", required = true)
    @NotNull private LocalDate birthdate;
    
    @Schema(description = "User address", required = true)
    @NotNull private String address;
    
    @Schema(description = "User phone number", required = true)
    @NotNull private String phone;
    
    @Schema(description = "User email address", required = true)
    @NotBlank @Email private String email;
    
    @Schema(description = "User base salary in Colombian pesos", required = true)
    @NotNull @DecimalMin("0.0") @DecimalMax("15000000.0") private BigDecimal baseSalary;
    
    @Schema(description = "User role ID")
    private String role;
}