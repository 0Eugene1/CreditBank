package com.example.deal.mapper;

import com.example.deal.dto.LoanStatementRequestDto;
import com.example.deal.entity.Client;
import com.example.deal.entity.Credit;
import com.example.deal.entity.Statement;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.CreditStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class StatementMapper {

    public Statement toEntity(LoanStatementRequestDto request, Client client, Credit credit) {
        return Statement.builder()
                .client(client)
                .credit(credit)
                .status(ApplicationStatus.PREPARE_DOCUMENTS)
                .creationDate(LocalDateTime.now())
                .build();
    }

    public Credit toCreditEntity(LoanStatementRequestDto request) {

        Credit credit = new Credit();
        // Присваиваем все необходимые поля из request
        credit.setAmount(request.getAmount());
        credit.setTerm(request.getTerm());
        credit.setCreditStatus(CreditStatus.CALCULATED);
        credit.setRate(BigDecimal.valueOf(10.0));
        credit.setPsk(BigDecimal.valueOf(10));
        credit.setMonthlyPayment(BigDecimal.valueOf(10));

        return credit;
    }
}
