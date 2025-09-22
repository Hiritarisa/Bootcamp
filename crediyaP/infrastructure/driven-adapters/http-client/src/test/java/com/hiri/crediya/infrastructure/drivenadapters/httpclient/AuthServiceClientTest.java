package com.hiri.crediya.infrastructure.drivenadapters.httpclient;

import com.hiri.crediya.model.auth.RoleValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthServiceClient.
 * Uses WebClient mocking to avoid external service dependencies.
 * This ensures tests are fast, reliable, and don't depend on external services.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceClientTest {

    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private AuthServiceClient authServiceClient;

    @BeforeEach
    void setUp() {
        authServiceClient = new AuthServiceClient(webClient);
        // Set the auth service URL for testing
        ReflectionTestUtils.setField(authServiceClient, "authServiceUrl", "http://localhost:8080");
        
        // Setup WebClient mock chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void shouldValidateAdminRoleSuccessfully() {
        // Given
        String token = "valid-admin-token";
        RoleValidationResponse response = new RoleValidationResponse(true, "Authorized");
        when(responseSpec.bodyToMono(RoleValidationResponse.class)).thenReturn(Mono.just(response));

        // When & Then
        StepVerifier.create(authServiceClient.validateAdminRole(token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenAdminRoleValidationFails() {
        // Given
        String token = "invalid-admin-token";
        RoleValidationResponse response = new RoleValidationResponse(false, "Unauthorized");
        when(responseSpec.bodyToMono(RoleValidationResponse.class)).thenReturn(Mono.just(response));

        // When & Then
        StepVerifier.create(authServiceClient.validateAdminRole(token))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldHandleAdminRoleValidationError() {
        // Given
        String token = "error-token";
        when(responseSpec.bodyToMono(RoleValidationResponse.class))
                .thenReturn(Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        // When & Then
        StepVerifier.create(authServiceClient.validateAdminRole(token))
                .expectError(WebClientResponseException.class)
                .verify();
    }

    @Test
    void shouldValidateAdvisorRoleSuccessfully() {
        // Given
        String token = "valid-advisor-token";
        RoleValidationResponse response = new RoleValidationResponse(true, "Authorized");
        when(responseSpec.bodyToMono(RoleValidationResponse.class)).thenReturn(Mono.just(response));

        // When & Then
        StepVerifier.create(authServiceClient.validateAdvisorRole(token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenAdvisorRoleValidationFails() {
        // Given
        String token = "invalid-advisor-token";
        RoleValidationResponse response = new RoleValidationResponse(false, "Unauthorized");
        when(responseSpec.bodyToMono(RoleValidationResponse.class)).thenReturn(Mono.just(response));

        // When & Then
        StepVerifier.create(authServiceClient.validateAdvisorRole(token))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldHandleAdvisorRoleValidationError() {
        // Given
        String token = "error-token";
        when(responseSpec.bodyToMono(RoleValidationResponse.class))
                .thenReturn(Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        // When & Then
        StepVerifier.create(authServiceClient.validateAdvisorRole(token))
                .expectError(WebClientResponseException.class)
                .verify();
    }

    @Test
    void shouldValidateClientRoleSuccessfully() {
        // Given
        String token = "valid-client-token";
        RoleValidationResponse response = new RoleValidationResponse(true, "Authorized");
        when(responseSpec.bodyToMono(RoleValidationResponse.class)).thenReturn(Mono.just(response));

        // When & Then
        StepVerifier.create(authServiceClient.validateClientRole(token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenClientRoleValidationFails() {
        // Given
        String token = "invalid-client-token";
        RoleValidationResponse response = new RoleValidationResponse(false, "Unauthorized");
        when(responseSpec.bodyToMono(RoleValidationResponse.class)).thenReturn(Mono.just(response));

        // When & Then
        StepVerifier.create(authServiceClient.validateClientRole(token))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldHandleClientRoleValidationError() {
        // Given
        String token = "error-token";
        when(responseSpec.bodyToMono(RoleValidationResponse.class))
                .thenReturn(Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        // When & Then
        StepVerifier.create(authServiceClient.validateClientRole(token))
                .expectError(WebClientResponseException.class)
                .verify();
    }
}
