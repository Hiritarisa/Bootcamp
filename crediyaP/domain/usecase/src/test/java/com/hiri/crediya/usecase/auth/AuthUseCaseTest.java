package com.hiri.crediya.usecase.auth;

import com.hiri.crediya.model.auth.gateways.AuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {
    
    @Mock
    private AuthRepository authRepository;
    
    @InjectMocks
    private AuthUseCase authUseCase;
    
    @Test
    void shouldValidateAdminRoleSuccessfully() {
        // Given
        String token = "valid-token";
        when(authRepository.validateAdminAdvisorRole(token)).thenReturn(Mono.just(true));
        
        // When & Then
        StepVerifier.create(authUseCase.isAdminOrAdvisor(token))
                .expectNext(true)
                .verifyComplete();
    }
    
    @Test
    void shouldValidateClientRoleSuccessfully() {
        // Given
        String token = "valid-token";
        when(authRepository.validateClientRole(token)).thenReturn(Mono.just(true));
        
        // When & Then
        StepVerifier.create(authUseCase.isClient(token))
                .expectNext(true)
                .verifyComplete();
    }
    
    @Test
    void shouldReturnFalseWhenRoleValidationFails() {
        // Given
        String token = "invalid-token";
        when(authRepository.validateAdminAdvisorRole(token)).thenReturn(Mono.just(false));
        
        // When & Then
        StepVerifier.create(authUseCase.isAdminOrAdvisor(token))
                .expectNext(false)
                .verifyComplete();
    }
}
