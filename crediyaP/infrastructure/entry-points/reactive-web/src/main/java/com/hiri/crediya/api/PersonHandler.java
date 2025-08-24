package com.hiri.crediya.api;

import com.hiri.crediya.api.dto.PersonRequest;
import com.hiri.crediya.api.dto.PersonResponse;
import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.usecase.personregistry.PersonUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonHandler {

    private final PersonUseCase personUseCase;
    private final Validator validator;

    public Mono<ServerResponse> create(ServerRequest req) {
        return req.bodyToMono(PersonRequest.class)
                .flatMap(this::validate)
                .map(this::toDomain)
                .flatMap(personUseCase::execute)
                .flatMap(p -> {
                    log.info("Persona creada id={}", p.getId());
                    return ServerResponse.created(URI.create("/api/v1/usuarios/" + p.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(toResponse(p));
                });
    }

    private Mono<PersonRequest> validate(PersonRequest r) {
        var result = validator.validate(r);
        if (!result.isEmpty()) {
            var ex = new BindException(r, "personRequest");
            result.forEach(v -> ex.rejectValue(v.getPropertyPath().toString(), "invalid", v.getMessage()));
            return Mono.error(ex);
        }
        return Mono.just(r);
    }

    private Person toDomain(PersonRequest r) {
        return Person.builder()
                .names(r.getNames())
                .lastnames(r.getLastnames())
                .birthdate(r.getBirthdate())
                .address(r.getAddress())
                .phone(r.getPhone())
                .email(r.getEmail())
                .baseSalary(r.getBaseSalary())
                .build();
    }

    private PersonResponse toResponse(Person p) {
        return PersonResponse.builder()
                .id(p.getId())
                .names(p.getNames())
                .lastnames(p.getLastnames())
                .email(p.getEmail())
                .build();
    }
}