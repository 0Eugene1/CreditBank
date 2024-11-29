package com.example.calculator.service;

import com.example.calculator.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanCalcService {

    private static final Logger logger = LoggerFactory.getLogger(LoanCalcService.class);

    private final ScoringService scoringService;
    private final PrescoringService prescoringService;


    @Value("${loan.base-rate}")
    private double baseRate;

    public LoanCalcService(ScoringService scoringService, PrescoringService prescoringService) {
        this.prescoringService = prescoringService;
        this.scoringService = scoringService;
    }
    public void printBaseRate() {
        // Логируем значение baseRate
        logger.info("Loan base rate: " + baseRate);
    }

    @Operation(summary = "Calculate Credit Details", description = "Calculates credit details including rate, monthly payment, PSK, and payment schedule.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated"),
            @ApiResponse(responseCode = "400", description = "Bad request, prescoring validation failed")
    })
    public CreditDto calculateCredit(ScoringDataDto scoringData) {
      logger.info("Start calculateCredit method");


      logger.info("Received scoringData: {}", scoringData);
        if (!prescoringService.validate(scoringData)) {
            logger.warn("Prescoring failed for scoringData: {}", scoringData);
            throw new IllegalArgumentException("Предварительная проверка заявки не пройдена");
        }
        // Рассчитываем % ставку rate
        double rate = baseRate + scoringService.calculateRate(scoringData);
        logger.info("Calculated rate: {}", rate);

        // Рассчитываем ежемесячный платеж
        BigDecimal amount = scoringData.getAmount(); // Сумма кредита
        int term = scoringData.getTerm(); // Срок кредита
        double monthlyRate = rate / 100 / 12; // Месячная % ставка
        //Рассчитываем ежемесячный платеж
        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, monthlyRate, term);
        logger.info("Monthly payment: {}", monthlyPayment);

        //Рассчитываем полную стоимость кредита PSK
        BigDecimal psk = monthlyPayment.multiply(BigDecimal.valueOf(term)); // сумма всех платежей
        logger.info("PSK - total cost calculated: {}", psk);

        // График ежемесячных платежей
        List<PaymentScheduleElementDto> paymentSchedule = createPaymentSchedule(amount, monthlyRate, term, monthlyPayment);
        logger.info("Payment schedule created: {}", paymentSchedule);

        CreditDto creditDto = new CreditDto();
        creditDto.setRate(BigDecimal.valueOf(rate)); //Ставка
        creditDto.setAmount(amount); //Сумма кредита
        creditDto.setMonthlyPayment(monthlyPayment); //Ежемесяынй платеж
        creditDto.setPsk(psk); //psk
        creditDto.setIsInsuranceEnabled(scoringData.getIsInsuranceEnabled()); //Наличие страховки
        creditDto.setIsSalaryClient(scoringData.getIsSalaryClient()); //Является ли клиент зарплатным
        creditDto.setPaymentSchedule(paymentSchedule);//График платежей

        logger.info("Credit details calculated successfully: {}", creditDto);

        return creditDto;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, double monthlyRate, int term) {
        logger.debug("Calculating monthly payment for amount: {}, monthlyRate: {}, term: {}", amount, monthlyRate, term);
        // Рассчитываем ежемесячный платеж

        BigDecimal rateFactor = BigDecimal.valueOf(1 + monthlyRate);
        logger.debug("Rate factor: {}", rateFactor);

        BigDecimal denominator = rateFactor.pow(term).subtract(BigDecimal.ONE);
        logger.debug("Denominator: {}", denominator);

        BigDecimal numerator = amount.multiply(BigDecimal.valueOf(monthlyRate)).multiply(rateFactor.pow(term));
        logger.debug("Numerator: {}", numerator);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP); //Округление до 2х знаков после запятой
    }

    private List<PaymentScheduleElementDto> createPaymentSchedule(BigDecimal amount, double monthlyRate, int term,
                                                                  BigDecimal monthlyPayment) {
        logger.debug("Creating payment schedule for amount: {}, monthlyRate: {}, term: {}, monthlyPayment: {}",
                amount, monthlyRate, term, monthlyPayment);

        List<PaymentScheduleElementDto> schedule = new ArrayList<>();
        BigDecimal remainingDebt = amount;

        for (int i = 1; i <= term; i++) {
            //Рассчитываем % и основную часть платежа для каждого месяца
            BigDecimal interestPayment = remainingDebt.multiply(BigDecimal.valueOf(monthlyRate)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);
            remainingDebt = remainingDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);

            //лог данных по каждому месяцу
            logger.debug("Month {}: interestPayment = {}, debtPayment = {}, remainingDebt = {}", i, interestPayment, debtPayment, remainingDebt);


            //Создаем элемент для графика
            PaymentScheduleElementDto element = new PaymentScheduleElementDto();
            element.setDate(LocalDate.now().plusMonths(i)); //Дата платежа
            element.setNumber(i); // Номер месяца
            element.setTotalPayment(monthlyPayment); // Общий платеж
            element.setInterestPayment(interestPayment); // проценты
            element.setDebtPayment(debtPayment); // Основной долг
            element.setRemainingDebt(remainingDebt); // Основной долг

            schedule.add(element);
        }
        logger.debug("Payment schedule created: {}", schedule);
        return schedule;
    }
}
