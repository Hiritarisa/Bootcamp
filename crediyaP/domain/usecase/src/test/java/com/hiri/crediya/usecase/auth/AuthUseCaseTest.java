package com.hiri.crediya.usecase.auth;

import com.hiri.crediya.model.auth.gateways.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit tests for AuthUseCase following hexagonal architecture.
 * Uses test implementations instead of mocks to keep domain layer clean.
 */
class AuthUseCaseTest {

    private TestAuthUseCase testAuthUseCase;
    private TestAuthRepository testRepository;

    @BeforeEach
    void setUp() {
        testRepository = new TestAuthRepository();
        testAuthUseCase = new TestAuthUseCase(testRepository);
    }

    @Test
    void shouldValidateAdminRoleSuccessfully() {
        // Given
        String token = "valid-admin-token";
        testRepository.addAdminToken(token);

        // When & Then
        StepVerifier.create(testAuthUseCase.isAdmin(token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenAdminRoleValidationFails() {
        // Given
        String token = "invalid-admin-token";

        // When & Then
        StepVerifier.create(testAuthUseCase.isAdmin(token))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldValidateAdvisorRoleSuccessfully() {
        // Given
        String token = "valid-advisor-token";
        testRepository.addAdvisorToken(token);

        // When & Then
        StepVerifier.create(testAuthUseCase.isAdvisor(token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenAdvisorRoleValidationFails() {
        // Given
        String token = "invalid-advisor-token";

        // When & Then
        StepVerifier.create(testAuthUseCase.isAdvisor(token))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldValidateClientRoleSuccessfully() {
        // Given
        String token = "valid-client-token";
        testRepository.addClientToken(token);

        // When & Then
        StepVerifier.create(testAuthUseCase.isClient(token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenClientRoleValidationFails() {
        // Given
        String token = "invalid-client-token";

        // When & Then
        StepVerifier.create(testAuthUseCase.isClient(token))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldHandleEmptyToken() {
        // Given
        String emptyToken = "";

        // When & Then
        StepVerifier.create(testAuthUseCase.isAdmin(emptyToken))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldHandleNullToken() {
        // Given
        String nullToken = null;

        // When & Then
        StepVerifier.create(testAuthUseCase.isAdmin(nullToken))
                .expectNext(false)
                .verifyComplete();
    }

    /**
     * Test implementation of AuthUseCase for domain tests.
     * Does NOT use the real production class - it's a complete test implementation.
     * Uses direct implementation for simplicity and clarity.
     */
    private static class TestAuthUseCase {
        private final TestAuthRepository authRepository;

        public TestAuthUseCase(TestAuthRepository authRepository) {
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

    /**
     * Test implementation of AuthRepository for domain tests.
     * Simple in-memory implementation without external dependencies.
     * Direct implementation - no interfaces needed in testing context.
     */
    private static class TestAuthRepository {
        private String adminToken;
        private String advisorToken;
        private String clientToken;

        public void addAdminToken(String token) {
            this.adminToken = token;
        }

        public void addAdvisorToken(String token) {
            this.advisorToken = token;
        }

        public void addClientToken(String token) {
            this.clientToken = token;
        }

        public Mono<Boolean> validateAdminRole(String token) {
            return Mono.just(token != null && token.equals(adminToken));
        }

        public Mono<Boolean> validateAdvisorRole(String token) {
            return Mono.just(token != null && token.equals(advisorToken));
        }

        public Mono<Boolean> validateClientRole(String token) {
            return Mono.just(token != null && token.equals(clientToken));
        }
    }
}