package com.example.deal.mapper;

import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.ScoringDataDto;
import com.example.deal.entity.Statement;

public class ScoringDataMapper {

    public static ScoringDataDto toScoringDataDto(FinishRegistrationRequestDto registrationRequest, Statement statement) {

        return ScoringDataDto.builder()
                // Данные из Statement
                .amount(statement.getCredit().getAmount()) // Сумма кредита
                .term(statement.getCredit().getTerm()) // Срок кредита
                .firstName(statement.getClient().getFirstName()) // Имя клиента
                .lastName(statement.getClient().getLastName()) // Фамилия клиента
                .middleName(statement.getClient().getMiddleName()) // Отчество клиента (если есть)
                .birthDate(statement.getClient().getBirthDate()) // Дата рождения клиента или значение по умолчанию
                .passportSeries(statement.getClient() != null && statement.getClient().getPassport() != null
                        ? statement.getClient().getPassport().getSeries() : "UNKNOWN") // Серия паспорта
                .passportNumber(statement.getClient() != null && statement.getClient().getPassport() != null
                        ? statement.getClient().getPassport().getNumber() : "UNKNOWN") // Номер паспорта
                .isInsuranceEnabled(statement.getCredit().isInsuranceEnabled()) // Наличие страховки
                .isSalaryClient(statement.getCredit().isSalaryClient()) // Клиент получает зарплату через банк

                // Данные из FinishRegistrationRequestDto
                .accountNumber(registrationRequest.getAccountNumber()) // Номер аккаунта
                .gender(registrationRequest.getGender()) // Пол
                .employment(registrationRequest.getEmployment()) // Информация о занятости
                .maritalStatus(registrationRequest.getMaritalStatus()) // Семейное положение
                .dependentAmount(registrationRequest.getDependentAmount()) // Количество иждивенцев
                .passportIssueBranch(registrationRequest.getPassportIssueBranch()) // Отделение выдачи паспорта
                .passportIssueDate(registrationRequest.getPassportIssueDate()) // Дата выдачи паспорта
                .build();
    }
}
