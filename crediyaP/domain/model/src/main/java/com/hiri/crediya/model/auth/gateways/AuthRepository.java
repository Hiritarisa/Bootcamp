package com.hiri.crediya.model.auth.gateways;

import reactor.core.publisher.Mono;

public interface AuthRepository {
    Mono<Boolean> validateAdminAdvisorRole(String token);
    Mono<Boolean> validateClientRole(String token);
}
