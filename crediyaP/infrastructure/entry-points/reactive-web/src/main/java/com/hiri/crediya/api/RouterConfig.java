package com.hiri.crediya.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/usuarios", beanClass = PersonHandler.class, beanMethod = "create")
    })
    public RouterFunction<ServerResponse> routes(PersonHandler handler) {
        return route(POST("/api/v1/usuarios").and(accept(MediaType.APPLICATION_JSON)), handler::create);
    }
}