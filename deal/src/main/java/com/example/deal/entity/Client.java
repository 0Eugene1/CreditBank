package com.example.deal.entity;

import com.example.deal.enums.Gender;
import com.example.deal.enums.MaritalStatus;
import com.example.deal.json.Employment;
import com.example.deal.json.Passport;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Client {

    @Id
    @GeneratedValue
    private UUID clientId;

    private String lastName;
    private String firstName;
    private String middleName;

    @NotNull
    private LocalDate birthDate;
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;


    private int dependentAmount;

    @OneToOne
    @JoinColumn(name = "passport_id")
    @NotNull
    private Passport passport;

    @OneToOne
    @JoinColumn(name = "employment_id")
    private Employment employment;

    private String accountNumber;

}
