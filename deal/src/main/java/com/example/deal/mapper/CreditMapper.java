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

        // Проверка значений перед присваиванием
        if (creditDto.getAmount() == null) {
            throw new IllegalArgumentException("Сумма кредита не может быть нулевой");
        }
        credit.setAmount(creditDto.getAmount());

        if (creditDto.getPsk() == null) {
            throw new IllegalArgumentException("PSK не может быть нулевым");
        }
        credit.setPsk(creditDto.getPsk());

        if (creditDto.getRate() == null) {
            throw new IllegalArgumentException("Ставка не может быть нулевой");
        }
        credit.setRate(creditDto.getRate());

        if (creditDto.getMonthlyPayment() == null) {
            throw new IllegalArgumentException("Ежемесячный платеж не может быть нулевым");
        }
        credit.setMonthlyPayment(creditDto.getMonthlyPayment());

        // Устанавливаем другие поля
        credit.setInsuranceEnabled(creditDto.isInsuranceEnabled());
        credit.setSalaryClient(creditDto.isSalaryClient());
        credit.setPaymentSchedule(creditDto.getPaymentSchedule());
        credit.setTerm(creditDto.getTerm());
        credit.setCreditStatus(CreditStatus.CALCULATED);

        log.info("Credit entity created successfully: {}", credit);
        return credit;
    }
}