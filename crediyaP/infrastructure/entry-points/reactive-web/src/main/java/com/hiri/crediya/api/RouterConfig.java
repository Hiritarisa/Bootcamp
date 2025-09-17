package com.hiri.crediya.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/usuarios", beanClass = PersonHandler.class, beanMethod = "create", method = RequestMethod.POST),
            @RouterOperation(path = "/api/v1/usuarios/{document}", beanClass = PersonHandler.class, beanMethod = "getPerson", method = RequestMethod.GET),
            @RouterOperation(path = "/api/v1/usuarios", beanClass = PersonHandler.class, beanMethod = "getAllPersons", method = RequestMethod.GET),
            @RouterOperation(path = "/api/v1/usuarios/{id}", beanClass = PersonHandler.class, beanMethod = "delete", method = RequestMethod.DELETE),
            @RouterOperation(path = "/api/v1/usuarios", beanClass = PersonHandler.class, beanMethod = "update", method = RequestMethod.PATCH)
    })
    public RouterFunction<ServerResponse> routes(PersonHandler handler) {
        return RouterFunctions
                .route()
                .path("/api/v1", builder -> builder
                        .POST("/usuarios", handler::create)
                        .GET("/usuarios/{document}", handler::getPerson)
                        .GET("/usuarios", handler::getAllPersons)
                        .DELETE("/usuarios/{id}", handler::delete)
                        .PATCH("/usuarios", handler::update))
                .build();
    }
}