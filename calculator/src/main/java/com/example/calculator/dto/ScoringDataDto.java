package com.example.calculator.dto;

import com.example.calculator.enums.GenderEnum;
import com.example.calculator.enums.MaritalStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class ScoringDataDto {

    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private GenderEnum gender;
    private LocalDate birthDate;
    private String passportSeries;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatusEnum maritalStatus;
    private Integer dependentAmount;
    private EmploymentDto employment;
    private String accountNumber;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    @Override
    public String toString() {
        return "ScoringDataDto{" +
                "amount=" + amount +
                ", term=" + term +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                ", passportSeries='" + passportSeries + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", passportIssueDate=" + passportIssueDate +
                ", passportIssueBranch='" + passportIssueBranch + '\'' +
                ", maritalStatus=" + maritalStatus +
                ", dependentAmount=" + dependentAmount +
                ", employment=" + employment +
                ", accountNumber='" + accountNumber + '\'' +
                ", isInsuranceEnabled=" + isInsuranceEnabled +
                ", isSalaryClient=" + isSalaryClient +
                '}';
    }

}
