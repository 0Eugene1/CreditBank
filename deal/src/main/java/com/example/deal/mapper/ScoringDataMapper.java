package com.example.deal.mapper;

import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.ScoringDataDto;
import com.example.deal.entity.Statement;

public class ScoringDataMapper {

    public static ScoringDataDto toScoringDataDto(FinishRegistrationRequestDto registrationRequest, Statement statement) {
        return ScoringDataDto.builder()
                .amount(statement.getCredit().getAmount())
                .term(statement.getCredit().getTerm())
                .firstName(statement.getClient().getFirstName())
                .lastName(statement.getClient().getLastName())
                .middleName(statement.getClient().getMiddleName())
                .gender(registrationRequest.getGender())
                .birthDate(statement.getClient().getBirthDate())
                .passportSeries(statement.getClient().getPassport().getSeries())
                .passportNumber(statement.getClient().getPassport().getNumber())
                .passportIssueDate(registrationRequest.getPassportIssueDate())
                .passportIssueBranch(registrationRequest.getPassportIssueBranch())
                .maritalStatus(registrationRequest.getMaritalStatus())
                .dependentAmount(registrationRequest.getDependentAmount())
                .employment(registrationRequest.getEmployment())
                .accountNumber(registrationRequest.getAccountNumber())
                .isInsuranceEnabled(statement.getCredit().isInsuranceEnabled())
                .isSalaryClient(statement.getCredit().isSalaryClient())
                .build();
    }
}
