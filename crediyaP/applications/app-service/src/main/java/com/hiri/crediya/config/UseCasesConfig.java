package com.hiri.crediya.config;

import com.hiri.crediya.model.person.gateways.PersonRepository;
import com.hiri.crediya.usecase.personregistry.PersonUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public PersonUseCase personUseCase(PersonRepository repository) {
        return new PersonUseCase(repository);
    }
}