package com.hiri.crediya.r2dbc;

import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.model.person.gateways.PersonRepository;
import com.hiri.crediya.r2dbc.entity.PersonEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PersonReactiveRepositoryAdapter implements PersonRepository {
    private final TransactionalOperator tx; // ← transacción en INFRA
    private final PersonReactiveRepository r2dbc;

    @Override
    public Mono<Boolean> existsByEmailOrDocument(String email, String document) {
        return r2dbc.existsByEmailOrDocument(email, document);
    }

    @Override
    public Mono<Person> findByDocument(String document) {
        return r2dbc.findByDocument(document);
    }

    @Override
    public Mono<Person> findById(UUID id) {
        return r2dbc.getById(id);
    }

    @Override
    public Flux<Person> getAllPersons(int page, int size) {
        return r2dbc.getAllPersons(page, size);
    }

    @Override
    public Mono<Void> deletePerson(UUID id) {
        return r2dbc.deleteById(id)
                .as(tx::transactional);
    }

    @Override
    public Mono<Person> save(Person person) {
        PersonEntity data = toData(person);
        return r2dbc.save(data)
                .map(this::toDomain)
                .as(tx::transactional)
                .onErrorMap(DuplicateKeyException.class, e -> new RuntimeException("User already exists"));
    }

    private PersonEntity toData(Person u) {
        return PersonEntity.builder()
                .id(u.getId())
                .names(u.getNames())
                .lastnames(u.getLastnames())
                .password(u.getPassword())
                .document(u.getDocument())
                .birthdate(u.getBirthdate())
                .address(u.getAddress())
                .phone(u.getPhone())
                .email(u.getEmail())
                .baseSalary(u.getBaseSalary())
                .role(u.getRole())
                .build();
    }

    private Person toDomain(PersonEntity d) {
        return Person.builder()
                .id(d.getId())
                .names(d.getNames())
                .lastnames(d.getLastnames())
                .password(d.getPassword())
                .document(d.getDocument())
                .birthdate(d.getBirthdate())
                .address(d.getAddress())
                .phone(d.getPhone())
                .email(d.getEmail())
                .baseSalary(d.getBaseSalary())
                .role(d.getRole())
                .build();
    }
}