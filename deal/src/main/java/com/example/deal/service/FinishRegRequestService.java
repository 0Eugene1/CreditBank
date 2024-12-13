package com.example.deal.service;

import com.example.deal.dto.CreditDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.FinishRegistrationRequestDto;
import com.example.deal.dto.PaymentScheduleElementDto;
import com.example.deal.dto.ScoringDataDto;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.CreditStatus;
import com.example.deal.exception.StatementNotFoundException;
import com.example.deal.mccalculator.CalculatorClient;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.StatementRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class FinishRegRequestService {

    private final CreditRepository creditRepository;
    private final StatementRepository statementRepository;
    private final CalculatorClient calculateCredit;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void finishRegistration(String statementId, FinishRegistrationRequestDto registrationRequest) {
        log.info("Starting finishRegistration for statementId: {}, with request: {}", statementId, registrationRequest);

        // 1. Найти Statement по ID
        Statement statement = statementRepository.findById(UUID.fromString(statementId))
                .orElseThrow(() -> new StatementNotFoundException("Statement not found"));

        // 2. Создать ScoringDataDto
        ScoringDataDto scoringData = getInformation(registrationRequest, statement);

        // 3. Отправить ScoringDataDto в кредитный конвейер и получить CreditDto
        CreditDto creditDto = calculateCredit.sendScoringData(scoringData);

        // 4. Создать сущность Credit и сохранить в базу
        Credit credit = createCreditFromDto(creditDto);

        // 5. Обновить статус Statement
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        updateStatusHistory(statement);

        // 6. Сохранить Credit и Statement
        statement.setCredit(credit);
        statementRepository.save(statement);
        log.info("Statement updated and saved with new credit: {}", statement);
    }


    public ScoringDataDto getInformation(FinishRegistrationRequestDto registrationRequest,
                               Statement statement) {
        log.info("Creating ScoringDataDto from FinishRegistrationRequestDto and Statement.");

        if (statement.getClient() != null && statement.getClient().getPassport() == null) {
            log.error("Passport data missing for client: {} {}", statement.getClient().getFirstName(), statement.getClient().getLastName());
        }


        ScoringDataDto scoringData =  ScoringDataDto.builder()
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
        log.debug("Generated ScoringDataDto: {}", scoringData);
        return scoringData;

    }

    @Transactional
    public Credit createCreditFromDto(CreditDto creditDto) {
        log.info("Creating Credit entity from CreditDto: {}", creditDto);

        Credit credit = new Credit();
        credit.setAmount(creditDto.getAmount());
        credit.setPsk(creditDto.getPsk());
        credit.setRate(creditDto.getRate());
        credit.setInsuranceEnabled(creditDto.isInsuranceEnabled());
        credit.setSalaryClient(creditDto.isSalaryClient());
        // Десериализация paymentSchedule
        try {
            List<PaymentScheduleElementDto> paymentSchedule = objectMapper.readValue(
                    objectMapper.writeValueAsString(creditDto.getPaymentSchedule()),
                    new TypeReference<List<PaymentScheduleElementDto>>() {}
            );
            credit.setPaymentSchedule(paymentSchedule);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации: " + e.getMessage(), e);
        }

        credit.setMonthlyPayment(creditDto.getMonthlyPayment());
        credit.setTerm(creditDto.getTerm());
        credit.setCreditStatus(CreditStatus.CALCULATED);

        log.info("Creating Credit from DTO: {}", creditDto);
        return creditRepository.save(credit);
    }

    private void updateStatusHistory(Statement statement) {
        log.info("Updating status history for statement: {}", statement);

        String updatedHistory = statement.getStatusHistory() != null
                ? statement.getStatusHistory() + ", " + ApplicationStatus.PREAPPROVAL.name()
                : ApplicationStatus.PREAPPROVAL.name();
        statement.setStatusHistory(updatedHistory);
        log.info("Updated status history for statement {}: {}", statement, updatedHistory);
    }
}
