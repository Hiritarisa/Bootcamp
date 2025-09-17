package com.hiri.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@Schema(description = "User response information")
public class PersonResponse {
    
    @Schema(description = "Unique user identifier")
    UUID id;
    
    @Schema(description = "User first names")
    String names;
    
    @Schema(description = "User last names")
    String lastnames;
    
    @Schema(description = "User document number")
    String document;
    
    @Schema(description = "User email address")
    String email;
}