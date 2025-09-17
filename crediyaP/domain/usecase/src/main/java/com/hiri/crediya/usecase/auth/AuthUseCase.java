package com.hiri.crediya.usecase.auth;

import com.hiri.crediya.model.auth.gateways.AuthRepository;
import reactor.core.publisher.Mono;

public class AuthUseCase {
    
    private final AuthRepository authRepository;
    
    public AuthUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    
    public Mono<Boolean> isAdminOrAdvisor(String token) {
        return authRepository.validateAdminAdvisorRole(token);
    }
    
    public Mono<Boolean> isClient(String token) {
        return authRepository.validateClientRole(token);
    }
}
