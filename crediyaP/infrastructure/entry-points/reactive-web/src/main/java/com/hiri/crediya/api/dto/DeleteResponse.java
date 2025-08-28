package com.hiri.crediya.api.dto;

import lombok.Builder;
import lombok.Setter;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class DeleteResponse {
    String message;
}