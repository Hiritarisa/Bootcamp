package com.hiri.crediya.config;

import com.hiri.crediya.model.auth.gateways.AuthRepository;
import com.hiri.crediya.model.person.gateways.PersonRepository;
import com.hiri.crediya.usecase.auth.AuthUseCase;
import com.hiri.crediya.usecase.personregistry.PersonUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public PersonUseCase personUseCase(PersonRepository repository) {
        return new PersonUseCase(repository);
    }

    @Bean
    public AuthUseCase authUseCase(AuthRepository authRepository) {
        return new AuthUseCase(authRepository);
    }
}