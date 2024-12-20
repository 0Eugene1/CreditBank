package com.example.deal.mapper;

import com.example.deal.dto.CreditDto;
import com.example.deal.entity.Credit;
import com.example.deal.enums.CreditStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreditMapper {

    public Credit creditToEntity(CreditDto creditDto) {
        log.info("Creating Credit entity from CreditDto: {}", creditDto);

        Credit credit = new Credit();
        credit.setAmount(creditDto.getAmount());
        credit.setPsk(creditDto.getPsk());
        credit.setRate(creditDto.getRate());
        credit.setInsuranceEnabled(creditDto.isInsuranceEnabled());
        credit.setSalaryClient(creditDto.isSalaryClient());

        // Устанавливаем paymentSchedule напрямую
        credit.setPaymentSchedule(creditDto.getPaymentSchedule());

        credit.setMonthlyPayment(creditDto.getMonthlyPayment());
        credit.setTerm(creditDto.getTerm());
        credit.setCreditStatus(CreditStatus.CALCULATED);

        log.info("Creating Credit from DTO: {}", creditDto);
        return credit;
    }
}
