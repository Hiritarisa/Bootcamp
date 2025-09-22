package com.hiri.crediya.r2dbc;

import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.r2dbc.entity.PersonEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PersonReactiveRepositoryAdapter.
 * Uses mocks for external dependencies (TransactionalOperator, PersonReactiveRepository).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PersonReactiveRepositoryAdapterTest {

    @Mock
    private TransactionalOperator transactionalOperator;
    @Mock
    private PersonReactiveRepository personReactiveRepository;

    private PersonReactiveRepositoryAdapter adapter;
    private Person testPerson;
    private PersonEntity testPersonEntity;
    private UUID testId;

    @BeforeEach
    void setUp() {
        adapter = new PersonReactiveRepositoryAdapter(transactionalOperator, personReactiveRepository);

        testId = UUID.randomUUID();
        testPerson = Person.builder()
                .id(testId)
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

        testPersonEntity = PersonEntity.builder()
                .id(testId)
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

        // Mock transactional operator to just return the publisher
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionalOperator.transactional(any(Flux.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldSavePersonSuccessfully() {
        // Given
        when(personReactiveRepository.save(any(PersonEntity.class))).thenReturn(Mono.just(testPersonEntity));

        // When & Then
        StepVerifier.create(adapter.save(testPerson))
                .expectNextMatches(p -> p.getId().equals(testPerson.getId()))
                .verifyComplete();
    }

    @Test
    void shouldHandleDuplicateKeyExceptionOnSave() {
        // Given
        when(personReactiveRepository.save(any(PersonEntity.class)))
                .thenReturn(Mono.error(new DuplicateKeyException("Duplicate key")));

        // When & Then
        StepVerifier.create(adapter.save(testPerson))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldFindPersonByDocumentSuccessfully() {
        // Given
        when(personReactiveRepository.findByDocument(anyString())).thenReturn(Mono.just(testPerson));

        // When & Then
        StepVerifier.create(adapter.findByDocument(testPerson.getDocument()))
                .expectNextMatches(p -> p.getDocument().equals(testPerson.getDocument()))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenFindByDocumentNotFound() {
        // Given
        when(personReactiveRepository.findByDocument(anyString())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.findByDocument("nonexistent"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldFindAllPersonsSuccessfully() {
        // Given
        Person anotherPerson = Person.builder()
                .id(UUID.randomUUID())
                .document("87654321")
                .email("another@example.com")
                .build();
        when(personReactiveRepository.getAllPersons(any(Integer.class), any(Integer.class)))
                .thenReturn(Flux.just(testPerson, anotherPerson));

        // When & Then
        StepVerifier.create(adapter.getAllPersons(0, 10))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void shouldFindPersonByIdSuccessfully() {
        // Given
        when(personReactiveRepository.getById(any(UUID.class))).thenReturn(Mono.just(testPerson));

        // When & Then
        StepVerifier.create(adapter.findById(testId))
                .expectNextMatches(p -> p.getId().equals(testId))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenFindByIdNotFound() {
        // Given
        when(personReactiveRepository.getById(any(UUID.class))).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.findById(UUID.randomUUID()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldDeletePersonSuccessfully() {
        // Given
        when(personReactiveRepository.deleteById(any(UUID.class))).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.deletePerson(testId))
                .verifyComplete();
    }

    @Test
    void shouldCheckIfPersonExistsByEmailOrDocument() {
        // Given
        String email = "juan.perez@example.com";
        String document = "12345678";
        when(personReactiveRepository.existsByEmailOrDocument(email, document))
                .thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(adapter.existsByEmailOrDocument(email, document))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenPersonDoesNotExistByEmailOrDocument() {
        // Given
        String email = "nonexistent@example.com";
        String document = "00000000";
        when(personReactiveRepository.existsByEmailOrDocument(email, document))
                .thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(adapter.existsByEmailOrDocument(email, document))
                .expectNext(false)
                .verifyComplete();
    }
}
