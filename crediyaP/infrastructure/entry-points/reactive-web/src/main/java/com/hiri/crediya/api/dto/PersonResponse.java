package com.hiri.crediya.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class PersonResponse {
    UUID id;
    String names;
    String lastnames;
    String document;
    String email;
    String role;
}