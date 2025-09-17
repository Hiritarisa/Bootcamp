package com.hiri.crediya.api;

import com.hiri.crediya.api.dto.DeleteResponse;
import com.hiri.crediya.api.dto.PersonRequest;
import com.hiri.crediya.api.dto.PersonResponse;
import com.hiri.crediya.model.person.Person;
import com.hiri.crediya.usecase.personregistry.PersonUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Component
@RequiredArgsConstructor
@Tag(name = "Persons")
public class PersonHandler {

    private final PersonUseCase personUseCase;
    private final Validator validator;
    public final Integer DEFAULT_ROLE_ID = 3;

    @Operation(
            summary = "Create new user",
            description = "Creates a new user in the system",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @RequestBody(
                    description = "User data to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PersonRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully",
                            content = @Content(schema = @Schema(implementation = PersonResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public Mono<ServerResponse> create(ServerRequest req) {
        return req.bodyToMono(PersonRequest.class)
                .flatMap(this::validate)
                .map(this::toDomain)
                .flatMap(personUseCase::execute)
                .flatMap(p -> {
                    log.info("Person created id={}", p.getId());
                    return ServerResponse.created(URI.create("/api/v1/usuarios/" + p.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(toResponse(p));
                });
    }

    @Operation(
            summary = "Get user by document",
            description = "Gets user information by document number",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "document", description = "Document number", 
                    required = true, in = ParameterIn.PATH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found",
                            content = @Content(schema = @Schema(implementation = PersonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public Mono<ServerResponse> getPerson(ServerRequest req) {
        String document = req.pathVariable("document");
        return personUseCase.findByDocument(document)
                .map(this::toResponse)
                .flatMap(res -> {
                    log.info("Person found id={}", res.getId());
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res);
                });
    }

    @Operation(
            summary = "List users",
            description = "Gets a paginated list of users",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "page", description = "Page number", in = ParameterIn.QUERY),
                    @Parameter(name = "limit", description = "Items per page", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User list retrieved",
                            content = @Content(schema = @Schema(type = "array", implementation = PersonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public Mono<ServerResponse> getAllPersons(ServerRequest req) {
        int page = 1;
        int limit = 10;
        try {
            page = Integer.parseInt(Objects.requireNonNull(req.queryParams().getFirst("page")));
            limit = Integer.parseInt(Objects.requireNonNull(req.queryParams().getFirst("limit")));
        } catch (NumberFormatException e) {
            log.info("Error parsing query params page={} and limit={} will be used default values 1/10 respectively", page, limit);
        }

        // Postgre pages start at 0. this could be a solution
        page = page > 0 ? page - 1 : page;
        limit = (page > 0 ? page : 1) * limit;
        return personUseCase.getList(page, limit)
                .collectList()
                .flatMap(res -> {
                    log.info("Total persons found {}", res.size());
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res);
                });
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user from the system",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = @Parameter(name = "id", description = "User ID", 
                    required = true, in = ParameterIn.PATH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted",
                            content = @Content(schema = @Schema(implementation = DeleteResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public Mono<ServerResponse> delete(ServerRequest req) {
        UUID ID = UUID.fromString(req.pathVariable("id"));
        return personUseCase.delete(ID)
                .flatMap(id -> {
                    Person person = new Person();
                    person.setId(id);
                    log.info("Person delete id={}", id);
                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(toDelete(person));
                });
    }

    @Operation(
            summary = "Update user",
            description = "Updates user information",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @RequestBody(
                    description = "Updated user data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PersonRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "202", description = "User updated",
                            content = @Content(schema = @Schema(implementation = PersonResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public Mono<ServerResponse> update(ServerRequest req) {
        return req.bodyToMono(PersonRequest.class)
                .flatMap(this::validate)
                .map(this::toUpdate)
                .flatMap(personUseCase::update)
                .flatMap(p -> {
                    log.info("Person updated id={}", p.getId());
                    return ServerResponse.accepted().contentType(MediaType.APPLICATION_JSON).bodyValue(p);
                });
    }

    private Mono<PersonRequest> validate(PersonRequest r) {
        var violations = validator.validate(r);
        if (!violations.isEmpty()) {
            String msg = violations.iterator().next().getPropertyPath() + " " + violations.iterator().next().getMessage();
            return Mono.error(new ResponseStatusException(BAD_REQUEST, msg));
        }
        return Mono.just(r);
    }

    private Person toDomain(PersonRequest r) {
        return Person.builder()
                .names(r.getNames())
                .lastnames(r.getLastnames())
                .password(r.getPassword())
                .document(r.getDocument())
                .birthdate(r.getBirthdate())
                .address(r.getAddress())
                .phone(r.getPhone())
                .email(r.getEmail())
                .baseSalary(r.getBaseSalary())
                .role(BigInteger.valueOf(DEFAULT_ROLE_ID))
                .build();
    }

    private Person toUpdate(PersonRequest r) {
        Person domain = toDomain(r);
        return domain.toBuilder()
                .id(r.getId())
                .role(BigInteger.valueOf(Long.parseLong(r.getRole())))
                .build();
    }

    private PersonResponse toResponse(Person p) {
        return PersonResponse.builder()
                .id(p.getId())
                .names(p.getNames())
                .lastnames(p.getLastnames())
                .document(p.getDocument())
                .email(p.getEmail())
                .build();
    }

    private DeleteResponse toDelete(Person p) {
        return DeleteResponse.builder()
                .message("Deleted user " + p.getId())
                .build();
    }
}