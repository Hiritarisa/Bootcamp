package com.hiri.crediya.usecase.personregistry;

import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.model.person.gateways.PersonRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class PersonUseCase {
    private final PersonRepository repository;

    public Mono<Person> execute(Person person) {
        return validatePerson(person)
                .flatMap(p -> repository.findByEmail(p.getEmail())
                        .flatMap(exist -> exist
                            ? Mono.error(new PersonUseCaseException("Email already registered: "+ p.getEmail()))
                            : repository.save(p))
                );
    }

    private Mono<Person> validatePerson(Person u) {
        if (u == null) return Mono.error(new PersonUseCaseException("User Object required"));
        if (invalidField(u.getNames())) return Mono.error(new PersonUseCaseException("Names required"));
        if (invalidField(u.getLastnames())) return Mono.error(new PersonUseCaseException("Last names required"));
        if (invalidField(u.getDocument())) return Mono.error(new PersonUseCaseException("Document required"));
        if (invalidField(u.getEmail())) return Mono.error(new PersonUseCaseException("Email required"));
        if (u.getBaseSalary() == null) return Mono.error(new PersonUseCaseException("Base Salary required"));
        if (u.getBaseSalary().compareTo(BigDecimal.ZERO) < 0 || u.getBaseSalary().compareTo(new BigDecimal("15000000")) > 0)
            return Mono.error(new PersonUseCaseException("Base salary out of valid range [0, 15000000]"));
        if (!u.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            return Mono.error(new PersonUseCaseException("Invalid email"));
        return Mono.just(u);
    }

    private boolean invalidField(String v) { return v == null || v.trim().isEmpty(); }

    public static class PersonUseCaseException extends RuntimeException {
        public PersonUseCaseException(String msg) {super(msg);}
    }
}



