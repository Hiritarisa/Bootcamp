package com.hiri.crediya.usecase.personregistry;

import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.model.person.gateways.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import java.util.logging.Logger;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class PersonUseCase {
    private Logger log = Logger.getLogger(PersonUseCase.class.getName());
    private final PersonRepository personRepository;
    private final TransactionalOperator tx;

    public Mono<Person> savePerson(Person person) {
        validate(person);
        return personRepository.findByEmail(person.getEmail())
            .flatMap(foundPerson -> {
                if (foundPerson == null || foundPerson.equals("")) {
                    return personRepository.save(person);
                }
                return Mono.error(new EmailDuplicatedException("Email already exists"));
            }).doOnSuccess(saved -> log.info("Registry complete successfully"))
            .doOnError(e -> log.info("Registry throw error: "+ e.getMessage()))
            .as(tx::transactional);
    }

    public Mono<Person> updatePerson(Person person) {
        return personRepository.save(person);
    }

    private void validate(Person p) {
        if (isBlank(p.getNames()) || isBlank(p.getLastnames()) || isBlank(p.getEmail())) {
            throw new ValidationException("Names, Lastnames and email are mandatory");
        }
        if (p.getBaseSalary() == null) {
            throw new ValidationException("Base salary is mandatory");
        }
        var min = BigDecimal.ZERO;
        var max = new BigDecimal("15000000");
        if (p.getBaseSalary().compareTo(min) < 0 || p.getBaseSalary().compareTo(max) > 0) {
            throw new ValidationException("Base salary should be equals or be greater than 0 and lower than 15000000");
        }
        if (!p.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ValidationException("Invalid email format");
        }
    }

    private boolean isBlank(String v) { return v == null || v.isBlank(); }

    public static class EmailDuplicatedException extends RuntimeException {
        public EmailDuplicatedException(String m) { super(m); }
    }
    public static class ValidationException extends RuntimeException {
        public ValidationException(String m) { super(m); }
    }
}
