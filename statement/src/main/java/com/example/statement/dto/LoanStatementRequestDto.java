package com.example.statement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatementRequestDto {

    @Schema(description = "Сумма кредита", example = "20000")
    @NotNull(message = "amount не должно быть нулевым")
    private BigDecimal amount;

    @Schema(description = "Срок кредита", example = "24")
    @NotNull(message = "term не должно быть пустым")
    private Integer term;

    @Schema(description = "Имя", example = "Oleg")
    @NotEmpty(message = "firstName не должно быть пустым")
    private String firstName;

    @Schema(description = "Фамилия", example = "Dobrov")
    @NotEmpty(message = "lastName не должно быть пустым")
    private String lastName;

    private String middleName;

    @Schema(description = "Email", example = "olegDobrov@gmail.com")
    @NotEmpty(message = "email не должно быть пустым")
    private String email;

    @Schema(description = "Дата рождения", example = "1999-01-01")
    @NotNull(message = "birthdate не должно быть пустым")
    private LocalDate birthDate;

    @Pattern(regexp = "\\d{4}", message = "Серия паспорта должна состоять из 4 цифр")
    @Schema(description = "Серия паспорта", example = "1234")
    @NotEmpty(message = "passportSeries не должно быть пустым")
    private String passportSeries;

    @Pattern(regexp = "\\d{6}", message = "Номер паспорта должна состоять из 6 цифр")
    @Schema(description = "Номер паспорта", example = "567890")
    @NotEmpty(message = "passportNumber не должно быть пустым")
    private String passportNumber;
}
