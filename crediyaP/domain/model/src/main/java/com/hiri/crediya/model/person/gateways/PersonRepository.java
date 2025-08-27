package com.hiri.crediya.model.person.gateways;

import com.hiri.crediya.model.person.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {
    Mono<Person> save(Person person);
    Mono<Boolean> existsByEmailOrDocument(String email, String document);
    Mono<Person> findByDocument(String document);
    Flux<Person> getAllPersons(int page, int size);
}



