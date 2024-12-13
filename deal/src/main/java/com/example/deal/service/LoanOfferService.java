package com.example.deal.service;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.CreditStatus;
import com.example.deal.mccalculator.CalculatorClient;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.StatementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanOfferService {

    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final StatementRepository statementRepository;
    private final CalculatorClient calculatorClient;

    @Transactional
    //На основе LoanStatementRequestDto создаётся сущность Client и сохраняется в БД.
    public List<LoanOfferDto> createClientFromRequest(LoanStatementRequestDto request) {
        log.info("Processing LoanStatementRequestDto: {}", request);

        // Создаем клиента и заявление
        Client client = createClient(request);
        Statement statement = createStatement(request, client);

        // Получаем предложения по кредиту
        List<LoanOfferDto> loanOffers = calculatorClient.getLoanOffers(request);

        if (loanOffers.isEmpty()) {
            throw new IllegalStateException("Кредитные предложения не могут быть пустыми.");
        }

        //Связывание идентификатора выписки с предложениями по кредиту
        loanOffers.forEach(offer -> offer.setStatementId(statement.getStatementId()));

        // Сортируем предложения по сумме
        List<LoanOfferDto> sortedOffers = loanOffers.stream()
                .sorted(Comparator.comparing(LoanOfferDto::getTotalAmount))
                .collect(Collectors.toList());

        log.info("Loan offers sorted successfully for statementId: {}", statement.getStatementId());

        return sortedOffers;

    }

    private Client createClient(LoanStatementRequestDto request) {
        //Создание и сохранение Client
        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setMiddleName(request.getMiddleName());
        client.setEmail(request.getEmail());
        client.setBirthDate(request.getBirthDate());


        log.info("Saving new client : {}", client);
        return clientRepository.save(client);
    }


    private Statement createStatement(LoanStatementRequestDto request, Client client) {
        //Создание кредитного предложения
        Credit credit = new Credit();
        credit.setAmount(request.getAmount());
        credit.setTerm(request.getTerm());
        credit.setCreditStatus(CreditStatus.CALCULATED);

        Credit savedCredit;
        try {
            savedCredit = creditRepository.save(credit);
        } catch (Exception e) {
            log.error("Error saving credit: {}", e.getMessage());
            throw new RuntimeException("Failed to save credit", e);
        }


        //Создаётся Statement со связью на только что созданный Client и сохраняется в БД.
        Statement statement = Statement.builder()
                .client(client)
                .credit(credit)
                .status(ApplicationStatus.PREPARE_DOCUMENTS)
                .creationDate(LocalDateTime.now())
                .build();


        log.info("Saving new statement: {}", statement);
        Statement savedStatement;
        try {
            savedStatement = statementRepository.save(statement);
        } catch (Exception e) {
            log.error("Error saving statement: {}", e.getMessage());
            throw new RuntimeException("Failed to save statement", e);
        }

        return savedStatement;
    }
}
