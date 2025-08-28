package com.hiri.crediya.r2dbc;


import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.r2dbc.entity.PersonEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PersonReactiveRepository extends R2dbcRepository<PersonEntity, UUID> {

    Mono<Boolean> existsByEmailOrDocument(String email, String document);
    Mono<Person> findByDocument(String document);
    Mono<Void> deleteById(UUID id);
    Mono<Person> getById(UUID id);
    @Query("SELECT * FROM persons ORDER BY id LIMIT :limit OFFSET :offset")
    Flux<Person> getAllPersons(@Param("offset")int page, @Param("limit")int size);
}
