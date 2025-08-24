package com.hiri.crediya.model.person.gateways;

import com.hiri.crediya.model.person.Person;
import reactor.core.publisher.Mono;

public interface PersonRepository {
    Mono<Person> save(Person person);
    Mono<Person> findByEmail(String email);
}
