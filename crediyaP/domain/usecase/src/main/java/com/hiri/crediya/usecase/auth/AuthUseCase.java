package com.hiri.crediya.usecase.auth;

import com.hiri.crediya.model.auth.gateways.AuthRepository;
import reactor.core.publisher.Mono;

public class AuthUseCase {
    
    private final AuthRepository authRepository;
    
    public AuthUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }
    
    public Mono<Boolean> isAdmin(String token) {
        return authRepository.validateAdminRole(token);
    }

    public Mono<Boolean> isAdvisor(String token) {
        return authRepository.validateAdvisorRole(token);
    }
    
    public Mono<Boolean> isClient(String token) {
        return authRepository.validateClientRole(token);
    }
}
