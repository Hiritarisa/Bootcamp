package com.hiri.crediya.model.auth.gateways;

import reactor.core.publisher.Mono;

public interface AuthRepository {
    Mono<Boolean> validateAdminRole(String token);
    Mono<Boolean> validateAdvisorRole(String token);
    Mono<Boolean> validateClientRole(String token);
}
