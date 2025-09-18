package com.hiri.crediya.api.config;

import com.hiri.crediya.usecase.auth.AuthUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleAuthorizationFilter implements WebFilter {

    private final AuthUseCase authUseCase;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        System.out.println("Start RoleAuthorizationFilter");
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        // Endpoints públicos
        if (isPublicEndpoint(path)) {
            log.debug("Public endpoint accessed: {}", path);
            return chain.filter(exchange);
        }

        // Extraer token
        String token = extractTokenFromRequest(request);
        if (token == null) {
            log.warn("No token found in request to: {}", path);
            return errorResponse(exchange, "No token found in request");
        }

        // Validar según el endpoint
        if (requiresAdminRole(path, method)) {
            log.info("Validating admin role for endpoint: {} {}", method, path);
            return authUseCase.isAdmin(token)
                    .flatMap(isAuthorized -> {
                        if (isAuthorized) {
                            log.info("Admin role validated successfully for: {} {}", method, path);
                            return chain.filter(exchange);
                        } else {
                            log.warn("Admin role validation failed for: {} {}", method, path);
                            return errorResponse(exchange, "Admin role required");
                        }
                    })
                    .onErrorResume(error -> {
                        log.error("Error validating admin role: {}", error.getMessage());
                        return errorResponse(exchange, error.getMessage());
                    });
        }

        if (requiresClientRole(path, method)) {
            log.info("Validating client role for endpoint: {} {}", method, path);
            return authUseCase.isClient(token)
                    .flatMap(isAuthorized -> {
                        if (isAuthorized) {
                            log.info("Client role validated successfully for: {} {}", method, path);
                            return chain.filter(exchange);
                        } else {
                            log.warn("Client role validation failed for: {} {}", method, path);
                            return errorResponse(exchange, "Client role required");
                        }
                    })
                    .onErrorResume(error -> {
                        log.error("Error validating client role: {}", error.getMessage());
                        return errorResponse(exchange, error.getMessage());
                    });
        }

        // Para otros endpoints protegidos, solo validar que el token existe
        log.info("Token found for endpoint: {} {}", method, path);
        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/actuator") ||
                path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars") ||
                path.equals("/");
    }

    private boolean requiresAdminRole(String path, String method) {
        // Crear, eliminar y actualizar usuarios requiere ADMIN/ADVISOR
        return (path.startsWith("/api/v1/usuarios") && "POST".equals(method)) ||
                (path.startsWith("/api/v1/usuarios") && "DELETE".equals(method)) ||
                (path.startsWith("/api/v1/usuarios") && "PATCH".equals(method)) ||
                (path.startsWith("/api/v1/usuarios") && "GET".equals(method)) ||
                (path.startsWith("/api/v1/usuarios/") && "GET".equals(method));
    }

    private boolean requiresClientRole(String path, String method) {
        // Listado de usuarios y ver usuario específico por documento requiere CLIENT
        return (path.startsWith("/api/v1/usuarios") && "GET".equals(method));
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> errorResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"message\":\"%s\"}", message);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes()))
        );
    }
}
