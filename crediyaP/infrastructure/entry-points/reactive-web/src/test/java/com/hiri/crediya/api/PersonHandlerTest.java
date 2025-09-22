package com.hiri.crediya.api;

import com.hiri.crediya.api.dto.PersonRequest;
import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.usecase.personregistry.PersonUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PersonHandler.
 * Uses mocks for external dependencies (PersonUseCase, Validator).
 */
@ExtendWith(MockitoExtension.class)
class PersonHandlerTest {

    @Mock
    private PersonUseCase personUseCase;
    @Mock
    private Validator validator;

    private PersonHandler personHandler;
    private PersonRequest validPersonRequest;
    private Person validPerson;

    @BeforeEach
    void setUp() {
        personHandler = new PersonHandler(personUseCase, validator);
        
        validPerson = Person.builder()
                .id(UUID.randomUUID())
                .names("Juan")
                .lastnames("Pérez")
                .document("12345678")
                .password("password123")
                .email("juan.perez@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .birthdate(LocalDate.of(1990, 5, 15))
                .address("Calle 123")
                .phone("3001234567")
                .build();

        validPersonRequest = new PersonRequest();
        validPersonRequest.setNames("Juan");
        validPersonRequest.setLastnames("Pérez");
        validPersonRequest.setDocument("12345678");
        validPersonRequest.setPassword("password123");
        validPersonRequest.setEmail("juan.perez@example.com");
        validPersonRequest.setBaseSalary(new BigDecimal("5000000"));
        validPersonRequest.setBirthdate(LocalDate.of(1990, 5, 15));
        validPersonRequest.setAddress("Calle 123");
        validPersonRequest.setPhone("3001234567");
    }

    @Test
    void shouldCreatePersonSuccessfully() {
        // Given
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(PersonRequest.class)).thenReturn(Mono.just(validPersonRequest));
        when(validator.validate(any(PersonRequest.class))).thenReturn(Collections.emptySet());
        when(personUseCase.execute(any(Person.class))).thenReturn(Mono.just(validPerson));

        // When
        Mono<ServerResponse> responseMono = personHandler.create(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() {
        // Given
        ServerRequest serverRequest = mock(ServerRequest.class);
        PersonRequest invalidPersonRequest = new PersonRequest();
        @SuppressWarnings("unchecked")
        ConstraintViolation<PersonRequest> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getMessage()).thenReturn("Validation error");
        when(validator.validate(any(PersonRequest.class))).thenReturn(Set.of(violation));
        when(serverRequest.bodyToMono(PersonRequest.class)).thenReturn(Mono.just(invalidPersonRequest));

        // When
        Mono<ServerResponse> responseMono = personHandler.create(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof ResponseStatusException &&
                        ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.BAD_REQUEST))
                .verify();
    }

    @Test
    void shouldGetPersonByDocumentSuccessfully() {
        // Given
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable("document")).thenReturn("12345678");
        when(personUseCase.findByDocument("12345678")).thenReturn(Mono.just(validPerson));

        // When
        Mono<ServerResponse> responseMono = personHandler.getPerson(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void shouldGetAllPersonsSuccessfully() {
        // Given
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.queryParams()).thenReturn(new org.springframework.util.LinkedMultiValueMap<>() {{
            add("page", "1");
            add("limit", "10");
        }});
        when(personUseCase.getList(any(Integer.class), any(Integer.class))).thenReturn(Flux.just(validPerson));

        // When
        Mono<ServerResponse> responseMono = personHandler.getAllPersons(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void shouldDeletePersonSuccessfully() {
        // Given
        ServerRequest serverRequest = mock(ServerRequest.class);
        UUID personId = UUID.randomUUID();
        when(serverRequest.pathVariable("id")).thenReturn(personId.toString());
        when(personUseCase.delete(personId)).thenReturn(Mono.just(personId));

        // When
        Mono<ServerResponse> responseMono = personHandler.delete(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void shouldUpdatePersonSuccessfully() {
        // Given
        ServerRequest serverRequest = mock(ServerRequest.class);
        PersonRequest updateRequest = new PersonRequest();
        updateRequest.setId(validPerson.getId());
        updateRequest.setNames("Updated Name");
        updateRequest.setLastnames("Updated Lastname");
        updateRequest.setDocument("87654321");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPassword("newpassword");
        updateRequest.setBaseSalary(new BigDecimal("6000000"));
        updateRequest.setRole("3");
        
        when(serverRequest.bodyToMono(PersonRequest.class)).thenReturn(Mono.just(updateRequest));
        when(validator.validate(any(PersonRequest.class))).thenReturn(Collections.emptySet());
        when(personUseCase.update(any(Person.class))).thenReturn(Mono.just(validPerson.toBuilder().names("Updated Name").build()));

        // When
        Mono<ServerResponse> responseMono = personHandler.update(serverRequest);

        // Then
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().equals(HttpStatus.ACCEPTED))
                .verifyComplete();
    }
}
