package com.hiri.crediya.infrastructure.drivenadapters.httpclient;

import com.hiri.crediya.model.auth.RoleValidationResponse;
import com.hiri.crediya.model.auth.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceClient implements AuthRepository {
    
    private final WebClient webClient;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    @Override
    public Mono<Boolean> validateAdminRole(String token) {
        log.info("Calling auth service to validate admin role at: {}", authServiceUrl);
        return webClient.get()
                .uri(authServiceUrl + "/api/v1/validate/admin")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(RoleValidationResponse.class)
                .map(RoleValidationResponse::isAuthorized)
                .doOnSuccess(authorized -> log.info("Admin validation successful: {}", authorized))
                .doOnError(error -> log.error("Admin validation failed: {}", error.getMessage()));
    }

    @Override
    public Mono<Boolean> validateAdvisorRole(String token) {
        log.info("Calling auth service to validate advisor role at: {}", authServiceUrl);
        return webClient.get()
                .uri(authServiceUrl + "/api/v1/validate/advisor")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(RoleValidationResponse.class)
                .map(RoleValidationResponse::isAuthorized)
                .doOnSuccess(authorized -> log.info("Advisor validation successful: {}", authorized))
                .doOnError(error -> log.error("Advisor validation failed: {}", error.getMessage()));
    }
    
    @Override
    public Mono<Boolean> validateClientRole(String token) {
        log.info("Calling auth service to validate client role at: {}", authServiceUrl);
        return webClient.get()
                .uri(authServiceUrl + "/api/v1/validate/client")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(RoleValidationResponse.class)
                .map(RoleValidationResponse::isAuthorized)
                .doOnSuccess(authorized -> log.info("Client validation successful: {}", authorized))
                .doOnError(error -> log.error("Client validation failed: {}", error.getMessage()));
    }
}
