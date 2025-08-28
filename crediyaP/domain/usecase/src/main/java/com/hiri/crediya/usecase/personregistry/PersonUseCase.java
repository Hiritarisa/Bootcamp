package com.hiri.crediya.usecase.personregistry;

import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.model.person.gateways.PersonRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class PersonUseCase {
    private final PersonRepository repository;

    public Mono<Person> execute(Person person) {
        return validatePerson(person)
            .flatMap(p -> repository.existsByEmailOrDocument(p.getEmail(),p.getDocument())
                .flatMap(exist -> exist
                    ? Mono.error(new PersonUseCaseException("Person document or email already registered "+ p.getEmail() + "-" + p.getDocument() ))
                    : repository.save(p))
            );
    }

    public Mono<Person> findByDocument(String document){
        return repository.findByDocument(document)
                .switchIfEmpty(Mono.error(new PersonUseCaseException("Person not found: "+ document)));
    }

    public Flux<Person> getList(int page, int size){
        return repository.getAllPersons(page, size)
                .switchIfEmpty(Mono.error(new PersonUseCaseException("There are no persons in the system")));
    }

    public Mono<Person> findById(UUID id){
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new PersonUseCaseException("User not found: "+ id)));
    }

    public Mono<UUID> delete(UUID id){
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new PersonUseCaseException("User not found: "+ id)))
            .flatMap(p -> repository.deletePerson(p.getId()))
            .then(Mono.just(id));
    }

    public Mono<Person> update(Person p){
        return repository.existsByEmailOrDocument(p.getEmail(),p.getDocument())
            .flatMap(exist -> exist
            ? Mono.error(new PersonUseCaseException("Person document or email already registered "+ p.getEmail() + " - " + p.getDocument() ))
            : repository.save(p));
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



