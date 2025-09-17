package com.hiri.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Delete confirmation response")
public class DeleteResponse {
    
    @Schema(description = "Delete confirmation message")
    String message;
}