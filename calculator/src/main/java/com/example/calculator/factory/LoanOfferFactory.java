package com.example.calculator.factory;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.service.PrescoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanOfferFactory {

    @Value("${loan.base-rate:10.0}")
    private BigDecimal baseRate;
    private final PrescoringService prescoringService;

    public LoanOfferDto createOffer(LoanStatementRequestDto request, int term, boolean isInsuranceEnabled, boolean isSalaryClient) {
        log.info("Start creating an offer: term={}, isInsuranceEnabled={}, isSalaryClient={}", term, isInsuranceEnabled, isSalaryClient);

        // Проверка baseRate
        if (baseRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Base rate is not correctly configured.");
        }

        if (request.getAmount() == null || request.getTerm() == null) {
            throw new IllegalArgumentException("Amount or Term cannot be null");
        }


        // Модифицируем базовую ставку в зависимости от условий
        BigDecimal modifiedRate = baseRate;
        if (isInsuranceEnabled) {
            modifiedRate = modifiedRate.subtract(BigDecimal.valueOf(3));  // Если страховка, то уменьшаем ставку на 3
        }
        if (isSalaryClient) {
            modifiedRate = modifiedRate.subtract(BigDecimal.valueOf(1));  // Если зарплатный клиент, то уменьшаем ставку на 1
        }


        // Рассчитываем общую сумму кредита с учетом страховки
        BigDecimal totalAmount = request.getAmount();
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount cannot be null or non-positive");
        }
        if (isInsuranceEnabled) {
            totalAmount = totalAmount.add(new BigDecimal(100000));  // Если есть страховка, добавляем к сумме 100,000
        }

        // Проверка term
        if (term <= 0) {
            throw new IllegalArgumentException("Term cannot be zero or negative.");
        }

        // Ежемесячный платеж
        BigDecimal monthlyRate = modifiedRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment;
        try {
            BigDecimal numerator = totalAmount.multiply(monthlyRate);
            BigDecimal denominator = BigDecimal.ONE.subtract(
                    BigDecimal.ONE.add(monthlyRate).pow(-term, new MathContext(10))
            );
            monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Error when calculating monthly payment: неверные параметры срока или ставки", e);
        }
        log.debug("Monthly payment: {}", monthlyPayment);


        // Создаем DTO для предложения по кредиту
        LoanOfferDto offer = LoanOfferDto.builder()
                .statementId(null)
                .requestedAmount(request.getAmount())
                .totalAmount(totalAmount) // Устанавливаем общую сумму
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(modifiedRate) // Если ставка равна 0, присваиваем 0
                .isInsuranceEnabled(isInsuranceEnabled) // Устанавливаем наличие страховки
                .isSalaryClient(isSalaryClient) // Устанавливаем статус зарплатного клиента
                .build();

        // Дополнительная проверка на корректность данных
        if (offer.getRate() == null || offer.getMonthlyPayment() == null) {
            log.debug("Создано некорректное предложение: {}", offer);
        }
        return offer;
    }

    public List<LoanOfferDto> createOffers(LoanStatementRequestDto request, int term) {
        // Создаем разные варианты предложений
        List<LoanOfferDto> offers = new ArrayList<>();
        offers.add(createOffer(request, term, false, false));  // Без страховки и зарплатного проекта
        offers.add(createOffer(request, term, true, false));   // Со страховкой, без зарплатного проекта
        offers.add(createOffer(request, term, false, true));   // Без страховки, с зарплатным проектом
        offers.add(createOffer(request, term, true, true));    // Со страховкой и зарплатным проектом

        return offers;
    }

}
