package com.hiri.crediya.r2dbc;


import com.hiri.crediya.r2dbc.entity.PersonEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PersonReactiveRepository extends R2dbcRepository<PersonEntity, UUID> {
    Mono<Boolean> existsByEmail(String email);
}
