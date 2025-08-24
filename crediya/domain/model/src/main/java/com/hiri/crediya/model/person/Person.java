package com.hiri.crediya.model.person;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Person {
    private Long id;
    private String names;
    private String lastnames;
    private LocalDate birthdate;
    private String address;
    private String phone;
    private String email;
    private BigDecimal baseSalary;
}
