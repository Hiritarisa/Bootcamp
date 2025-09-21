package com.hiri.crediya.integration;

import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.model.person.gateways.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration tests for Person API endpoints.
 * Uses @MockBean to mock external dependencies (PersonRepository).
 */
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class PersonIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PersonRepository personRepository;

    @Test
    void shouldCreatePersonSuccessfully() {
        // Given
        Person mockPerson = Person.builder()
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
                .role(BigInteger.valueOf(3))
                .build();

        when(personRepository.existsByEmailOrDocument(anyString(), anyString())).thenReturn(Mono.just(false));
        when(personRepository.save(any(Person.class))).thenReturn(Mono.just(mockPerson));

        String requestBody = """
                {
                    "names": "Juan",
                    "lastnames": "Pérez",
                    "document": "12345678",
                    "password": "password123",
                    "email": "juan.perez@example.com",
                    "baseSalary": 5000000,
                    "birthdate": "1990-05-15",
                    "address": "Calle 123",
                    "phone": "3001234567"
                }
                """;

        // When & Then
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody()
                .jsonPath("$.names").isEqualTo("Juan")
                .jsonPath("$.lastnames").isEqualTo("Pérez");
    }

    @Test
    void shouldGetPersonByDocumentSuccessfully() {
        // Given
        Person mockPerson = Person.builder()
                .id(UUID.randomUUID())
                .names("Juan")
                .lastnames("Pérez")
                .document("12345678")
                .email("juan.perez@example.com")
                .build();

        when(personRepository.findByDocument("12345678")).thenReturn(Mono.just(mockPerson));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/usuarios/document/12345678")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.document").isEqualTo("12345678")
                .jsonPath("$.names").isEqualTo("Juan");
    }

    @Test
    void shouldGetAllPersonsSuccessfully() {
        // Given
        Person mockPerson1 = Person.builder()
                .id(UUID.randomUUID())
                .names("Juan")
                .lastnames("Pérez")
                .document("12345678")
                .email("juan.perez@example.com")
                .build();

        Person mockPerson2 = Person.builder()
                .id(UUID.randomUUID())
                .names("María")
                .lastnames("García")
                .document("87654321")
                .email("maria.garcia@example.com")
                .build();

        when(personRepository.getAllPersons(any(Integer.class), any(Integer.class)))
                .thenReturn(Flux.just(mockPerson1, mockPerson2));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/usuarios?page=1&limit=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    void shouldDeletePersonSuccessfully() {
        // Given
        UUID personId = UUID.randomUUID();
        Person mockPerson = Person.builder()
                .id(personId)
                .names("Juan")
                .lastnames("Pérez")
                .build();

        when(personRepository.findById(personId)).thenReturn(Mono.just(mockPerson));
        when(personRepository.deletePerson(personId)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/api/v1/usuarios/" + personId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Deleted user " + personId);
    }

    @Test
    void shouldUpdatePersonSuccessfully() {
        // Given
        UUID personId = UUID.randomUUID();
        Person mockPerson = Person.builder()
                .id(personId)
                .names("Juan Carlos")
                .lastnames("Pérez")
                .document("12345678")
                .password("password123")
                .email("juan.perez@example.com")
                .baseSalary(new BigDecimal("5000000"))
                .birthdate(LocalDate.of(1990, 5, 15))
                .address("Calle 123")
                .phone("3001234567")
                .role(BigInteger.valueOf(3))
                .build();

        when(personRepository.existsByEmailOrDocument(anyString(), anyString())).thenReturn(Mono.just(false));
        when(personRepository.save(any(Person.class))).thenReturn(Mono.just(mockPerson));

        String requestBody = """
                {
                    "id": "%s",
                    "names": "Juan Carlos",
                    "lastnames": "Pérez",
                    "document": "12345678",
                    "password": "password123",
                    "email": "juan.perez@example.com",
                    "baseSalary": 5000000,
                    "birthdate": "1990-05-15",
                    "address": "Calle 123",
                    "phone": "3001234567",
                    "role": "3"
                }
                """.formatted(personId);

        // When & Then
        webTestClient.put()
                .uri("/api/v1/usuarios/" + personId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.names").isEqualTo("Juan Carlos");
    }

    @Test
    void shouldReturnBadRequestForInvalidData() {
        // Given
        String invalidRequestBody = """
                {
                    "names": "",
                    "lastnames": "Pérez",
                    "document": "12345678",
                    "password": "password123",
                    "email": "invalid-email",
                    "baseSalary": 5000000,
                    "birthdate": "1990-05-15",
                    "address": "Calle 123",
                    "phone": "3001234567"
                }
                """;

        // When & Then
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnNotFoundWhenPersonNotExists() {
        // Given
        when(personRepository.findByDocument("nonexistent")).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/usuarios/document/nonexistent")
                .exchange()
                .expectStatus().isNotFound();
    }
}
