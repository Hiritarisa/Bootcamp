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
                            ? Mono.error(new DuplicatedEmailException(p.getEmail()))
                            : repository.save(p))
                );
    }

    private Mono<Person> validatePerson(Person u) {
        if (u == null) return Mono.error(new IllegalArgumentException("User required"));
        if (invalidField(u.getNames())) return Mono.error(new MandatoryFieldException("Names required"));
        if (invalidField(u.getLastnames())) return Mono.error(new MandatoryFieldException("Last names required"));
        if (invalidField(u.getEmail())) return Mono.error(new MandatoryFieldException("Email required"));
        if (u.getBaseSalary() == null) return Mono.error(new MandatoryFieldException("Base Salary required"));
        if (u.getBaseSalary().compareTo(BigDecimal.ZERO) < 0 || u.getBaseSalary().compareTo(new BigDecimal("15000000")) > 0)
            return Mono.error(new ValidationException("Base salary out of valid range [0, 15000000]"));
        if (!u.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            return Mono.error(new ValidationException("Invalid email"));
        return Mono.just(u);
    }

    private boolean invalidField(String v) { return v == null || v.trim().isEmpty(); }

    public static class DuplicatedEmailException extends RuntimeException {
        public DuplicatedEmailException(String email) { super("Email already exist: " + email); }
    }
    public static class MandatoryFieldException extends RuntimeException {
        public MandatoryFieldException(String campo) { super("Mandatory field: " + campo); }
    }
    public static class ValidationException extends RuntimeException {
        public ValidationException(String msg) { super(msg); }
    }
}



