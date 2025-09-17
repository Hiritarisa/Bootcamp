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
    public Mono<Boolean> validateAdminAdvisorRole(String token) {
        log.info("Calling auth service to validate admin/advisor role at: {}", authServiceUrl);
        return webClient.get()
                .uri(authServiceUrl + "/api/v1/validate/admin-advisor")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(RoleValidationResponse.class)
                .map(RoleValidationResponse::isAuthorized)
                .doOnSuccess(authorized -> log.info("Admin/advisor validation successful: {}", authorized))
                .doOnError(error -> log.error("Admin/advisor validation failed: {}", error.getMessage()));
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
