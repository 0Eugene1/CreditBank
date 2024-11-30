package com.example.calculator.factory;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.service.PrescoringService;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class LoanOfferFactory {

    @Value("${loan.base-rate:10.0}")
    private double baseRate;
    private final PrescoringService prescoringService;

    public LoanOfferFactory(PrescoringService prescoringService) {
        this.prescoringService = prescoringService;
    }

    public LoanOfferDto createOffer(LoanStatementRequestDto request, int term, boolean isInsuranceEnabled, boolean isSalaryClient) {
        log.info("Start creating an offer: term={}, isInsuranceEnabled={}, isSalaryClient={}", term, isInsuranceEnabled, isSalaryClient);

        // Проверка baseRate
        if (baseRate <= 0) {
            log.error("Base rate is not correctly configured: {}", baseRate);
            throw new IllegalStateException("Base rate is not correctly configured.");
        }

        if (!prescoringService.validate(request)) {
            log.warn("Prescoring failed for scoringData: {}", request);
            throw new IllegalArgumentException("Предварительная проверка заявки не пройдена");
        }
        if (request.getAmount() == null || request.getTerm() == null) {
            throw new IllegalArgumentException("Amount or Term cannot be null");
        }


        // Модифицируем базовую ставку в зависимости от условий
        double modifiedRate = baseRate;
        if (isInsuranceEnabled) {
            modifiedRate -= 3;  // Если страховка, то уменьшаем ставку на 3
        }
        if (isSalaryClient) {
            modifiedRate -= 1;  // Если зарплатный клиент, то уменьшаем ставку на 1
        }
        log.debug("Modified rate: {}", modifiedRate);

        // Рассчитываем общую сумму кредита с учетом страховки
        BigDecimal totalAmount = request.getAmount();
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Amount is null or non-positive: {}", totalAmount);
            throw new IllegalArgumentException("Amount cannot be null or non-positive");
        }
        if (isInsuranceEnabled) {
            totalAmount = totalAmount.add(new BigDecimal(100000));  // Если есть страховка, добавляем к сумме 100,000
        }
        log.debug("Total loan amount including insurance: {}", totalAmount);

        // Проверка term
        if (term <= 0) {
            log.error("Term is not valid: {}", term);
            throw new IllegalArgumentException("Term cannot be zero or negative.");
        }

        // Ежемесячный платеж
        double monthlyRate = modifiedRate / 100.0 / 12; // Ежемесячная % ставка. Преобразуем в ~ 0.01 и делим на 12;
        BigDecimal monthlyPayment;
        try {
            monthlyPayment = totalAmount.multiply(BigDecimal.valueOf(monthlyRate))
                    .divide(BigDecimal.valueOf(1 - Math.pow(1 + monthlyRate, -term)), 2, RoundingMode.HALF_UP);
        } catch (ArithmeticException e) {
            log.error("Error when calculating monthly payment: totalAmount={}, modifiedRate={}, term={}", totalAmount, modifiedRate, term, e);
            throw new IllegalArgumentException("Error when calculating monthly payment: неверные параметры срока или ставки", e);
        }
        log.debug("Monthly payment: {}", monthlyPayment);


        // Создаем DTO для предложения по кредиту
        LoanOfferDto offer = new LoanOfferDto();

        offer.setStatementId(null);
        offer.setRequestedAmount(request.getAmount());
        offer.setTotalAmount(totalAmount); // Устанавливаем общую сумму
        offer.setTerm(term);
        offer.setMonthlyPayment(monthlyPayment);
        offer.setRate(new BigDecimal(modifiedRate != 0 ? modifiedRate : 0)); // Если ставка равна 0, присваиваем 0
        offer.setIsInsuranceEnabled(isInsuranceEnabled); // Устанавливаем наличие страховки
        offer.setIsSalaryClient(isSalaryClient); // Устанавливаем статус зарплатного клиента

        log.debug("LoanOfferDto создан с параметрами: statementId=null, requestedAmount={}, totalAmount={}, term={}, monthlyPayment={}, rate={}, isInsuranceEnabled={}, isSalaryClient={}",
                request.getAmount(), totalAmount, term, monthlyPayment, modifiedRate, isInsuranceEnabled, isSalaryClient);
        // Дополнительная проверка на корректность данных
        if (offer.getRate() == null || offer.getMonthlyPayment() == null) {
            log.error("Создано некорректное предложение: {}", offer);
        }
        return offer;
    }
}
