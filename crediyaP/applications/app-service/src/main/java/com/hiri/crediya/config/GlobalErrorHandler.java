package com.hiri.crediya.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper mapper;

    public GlobalErrorHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status;
        Map<String, Object> body = new LinkedHashMap<>();

        // 1) Clasificar excepción y armar payload
        if (ex instanceof org.springframework.web.bind.support.WebExchangeBindException webEx) {
            status = HttpStatus.BAD_REQUEST;
            var errors = webEx.getFieldErrors().stream()
                    .map(fe -> Map.of(
                            "field", fe.getField(),
                            "message", fe.getDefaultMessage(),
                            "rejectedValue", fe.getRejectedValue()
                    ))
                    .toList();
            body.put("message", "Validation failed");
            body.put("errors", errors);
        } else if (ex instanceof org.springframework.validation.BindException bindEx) {
            status = HttpStatus.BAD_REQUEST;
            var errors = bindEx.getFieldErrors().stream()
                    .map(fe -> Map.of(
                            "field", fe.getField(),
                            "message", fe.getDefaultMessage(),
                            "rejectedValue", fe.getRejectedValue()
                    ))
                    .toList();
            body.put("message", "Validation failed");
            body.put("errors", errors);
        } else if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            body.put("message", rse.getReason() != null ? rse.getReason() : rse.getMessage());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            body.put("message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error");
        }

        // 2) Campos comunes
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("path", exchange.getRequest().getPath().value() + "("+exchange.getRequest().getMethod()+")");

        // 3) Escribir respuesta
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = mapper.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception writeErr) {
            byte[] fallback = "{\"status\":500,\"message\":\"Error writing JSON\"}"
                    .getBytes(StandardCharsets.UTF_8);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(fallback)));
        }
    }
}