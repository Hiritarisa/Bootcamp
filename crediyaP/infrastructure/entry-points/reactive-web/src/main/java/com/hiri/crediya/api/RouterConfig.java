package com.hiri.crediya.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/usuarios", beanClass = PersonHandler.class, beanMethod = "create"),
            @RouterOperation(path = "/api/v1/usuarios/{document}", beanClass = PersonHandler.class, beanMethod = "getPerson"),
            @RouterOperation(path = "/api/v1/usuarios", beanClass = PersonHandler.class, beanMethod = "getAllPersons"),
            @RouterOperation(path = "/api/v1/usuarios/{id}", beanClass = PersonHandler.class, beanMethod = "delete"),
            @RouterOperation(path = "/api/v1/usuarios", beanClass = PersonHandler.class, beanMethod = "update")
    })
    public RouterFunction<ServerResponse> routes(PersonHandler handler) {
        return route(POST("/api/v1/usuarios").and(accept(MediaType.APPLICATION_JSON)), handler::create)
                .andRoute(GET("/api/v1/usuarios/{document}"), handler::getPerson)
                .andRoute(GET("/api/v1/usuarios"), handler::getAllPersons)
                .andRoute(DELETE("/api/v1/usuarios/{id}"), handler::delete)
                .andRoute(PATCH("/api/v1/usuarios").and(accept(MediaType.APPLICATION_JSON)), handler::update);
    }
}