package com.example.deal.service;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.mapper.ClientMapper;
import com.example.deal.mapper.StatementMapper;
import com.example.deal.mccalculator.CalculatorClient;
import com.example.deal.repository.ClientRepository;
import com.example.deal.repository.CreditRepository;
import com.example.deal.repository.StatementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final ClientMapper clientMapper;
    private final StatementMapper statementMapper;

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
        // Используем маппер для создания клиента
        Client client = clientMapper.toEntity(request);

        log.info("Saving new client: {}", client);
        return clientRepository.save(client);
    }


    private Statement createStatement(LoanStatementRequestDto request, Client client) {
        // Создание Credit через маппер
        Credit credit = statementMapper.toCreditEntity(request);

        // Сохранение Credit в базе данных
        Credit savedCredit = creditRepository.save(credit);

        // Создание Statement через маппер
        Statement statement = statementMapper.toEntity(request, client, savedCredit);

        // Сохранение Statement в базе данных
        return statementRepository.save(statement);
    }
}
