package com.hiri.crediya.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    beanClass = PersonHandler.class,
                    beanMethod = "create",
                    method = RequestMethod.POST
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/{document}",
                    beanClass = PersonHandler.class,
                    beanMethod = "getPerson",
                    method = RequestMethod.GET
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    beanClass = PersonHandler.class,
                    beanMethod = "getAllPersons",
                    method = RequestMethod.GET
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/{id}",
                    beanClass = PersonHandler.class,
                    beanMethod = "delete",
                    method = RequestMethod.DELETE
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    beanClass = PersonHandler.class,
                    beanMethod = "update",
                    method = RequestMethod.PATCH
            )
    })
    public RouterFunction<ServerResponse> routes(PersonHandler handler) {
        return RouterFunctions
                .route()
                .path("/api/v1", builder -> builder
                        .POST("/api/v1/usuarios", handler::create)
                        .GET("/api/v1/usuarios/{document}", handler::getPerson)
                        .GET("/api/v1/usuarios", handler::getAllPersons)
                        .DELETE("/api/v1/usuarios/{id}", handler::delete)
                        .PATCH("/api/v1/usuarios", handler::update))
                .build();
    }
}