package com.example.deal.service;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.feignclient.CalculatorOffersClient;
import com.example.deal.mapper.ClientMapper;
import com.example.deal.mapper.StatementMapper;
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
    private final ClientMapper clientMapper;
    private final StatementMapper statementMapper;
    private final CalculatorOffersClient calculatorOffersClient;


    @Transactional
    //На основе LoanStatementRequestDto создаётся сущность Client и сохраняется в БД.
    public List<LoanOfferDto> createClientFromRequest(LoanStatementRequestDto request) {
        log.info("Processing LoanStatementRequestDto: {}", request);

        // Используем маппер для создания и сохранения клиента
        Client client = clientMapper.toEntity(request);
        client = clientRepository.save(client);  // Сохраняем клиента

        // Создаем кредит через маппер и сохраняем его
        Credit credit = statementMapper.toCreditEntity(request);
        Credit savedCredit = creditRepository.save(credit);  // Сохраняем кредит

        // Создаем заявление через маппер и сохраняем его
        Statement statement = statementMapper.toEntity(request, client, savedCredit);
        Statement savedStatement = statementRepository.save(statement);  // Сохраняем заявление

        // Получаем предложения по кредиту
        List<LoanOfferDto> loanOffers = calculatorOffersClient.getLoanOffers(request);

        if (loanOffers.isEmpty()) {
            throw new IllegalStateException("Кредитные предложения не могут быть пустыми.");
        }

        //Связывание идентификатора выписки с предложениями по кредиту
        loanOffers.forEach(offer -> offer.setStatementId(savedStatement.getStatementId()));

        // Сортируем предложения по сумме
        List<LoanOfferDto> sortedOffers = loanOffers.stream()
                .sorted(Comparator.comparing(LoanOfferDto::getTotalAmount))
                .collect(Collectors.toList());

        log.info("Loan offers sorted successfully for statementId: {}", statement.getStatementId());

        return sortedOffers;

    }
}