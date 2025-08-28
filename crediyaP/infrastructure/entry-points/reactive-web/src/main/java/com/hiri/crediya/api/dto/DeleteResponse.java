package com.hiri.crediya.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeleteResponse {
    String message;
}