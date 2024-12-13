package com.example.deal.entity;

import com.example.deal.enums.Gender;
import com.example.deal.enums.MaritalStatus;
import com.example.deal.json.Employment;
import com.example.deal.json.Passport;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID clientId;

    @NotBlank(message = "Фамилия не должна быть пустой")
    private String lastName;

    @NotBlank(message = "Имя не должно быть пустым")
    private String firstName;

    private String middleName;

    @NotNull(message = "Дата рождения не должна быть пустой")
    private LocalDate birthDate;

    @Email(message = "Некорректный формат email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    private int dependentAmount;

    @OneToOne
    @JoinColumn(name = "passport_id")
    @NotNull(message = "Паспорт не должен быть пустым")
    private Passport passport;

    @OneToOne
    @JoinColumn(name = "employment_id")
    private Employment employment;

    private String accountNumber;

    @OneToMany(mappedBy = "client")
    private List<Statement> statements; // Обратная связь
}



