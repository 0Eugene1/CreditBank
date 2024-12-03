package com.example.calculator.service;

import com.example.calculator.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanCalcService {

    private final ScoringService scoringService;
    private final PrescoringService prescoringService;

    @Value("${loan.base-rate}")
    private double baseRate;

    public CreditDto calculateCredit(ScoringDataDto scoringData) {
        log.info("Start calculateCredit method");


        log.info("Received scoringData: {}", scoringData);
        // Рассчитываем % ставку rate
        double rate = baseRate + scoringService.calculateRate(scoringData);
        log.info("Calculated rate: {}", rate);

        // Рассчитываем ежемесячный платеж
        BigDecimal amount = scoringData.getAmount(); // Сумма кредита
        int term = scoringData.getTerm(); // Срок кредита
        BigDecimal monthlyRate = BigDecimal.valueOf(rate)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP) // Преобразование в месячную % ставку
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        //Рассчитываем ежемесячный платеж
        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, monthlyRate, term);
        log.info("Monthly payment: {}", monthlyPayment);

        //Рассчитываем полную стоимость кредита PSK
        BigDecimal psk = monthlyPayment.multiply(BigDecimal.valueOf(term)); // сумма всех платежей
        log.info("PSK - total cost calculated: {}", psk);

        // График ежемесячных платежей
        List<PaymentScheduleElementDto> paymentSchedule = createPaymentSchedule(amount, monthlyRate, term, monthlyPayment);
        log.info("Payment schedule created: {}", paymentSchedule);

        CreditDto creditDto = CreditDto.builder()
                        .rate(BigDecimal.valueOf(rate))
                                .amount(amount)
                                        .monthlyPayment(monthlyPayment)
                                                .psk(psk)
                                                        .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                                                                .isSalaryClient(scoringData.getIsSalaryClient())
                                                                        .paymentSchedule(paymentSchedule)
                                                                                .build();
        log.info("Credit details calculated successfully: {}", creditDto);

        return creditDto;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal monthlyRate, int term) {
        log.debug("Calculating monthly payment for amount: {}, monthlyRate: {}, term: {}", amount, monthlyRate, term);
        // Рассчитываем ежемесячный платеж

        BigDecimal rateFactor = BigDecimal.ONE.add(monthlyRate);
        log.debug("Rate factor: {}", rateFactor);

        BigDecimal denominator = rateFactor.pow(term).subtract(BigDecimal.ONE);
        log.debug("Denominator: {}", denominator);

        BigDecimal numerator = amount.multiply(monthlyRate).multiply(rateFactor.pow(term));
        log.debug("Numerator: {}", numerator);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP); //Округление до 2х знаков после запятой
    }

    private List<PaymentScheduleElementDto> createPaymentSchedule(BigDecimal amount, BigDecimal monthlyRate, int term,
                                                                  BigDecimal monthlyPayment) {
        log.debug("Creating payment schedule for amount: {}, monthlyRate: {}, term: {}, monthlyPayment: {}",
                amount, monthlyRate, term, monthlyPayment);

        List<PaymentScheduleElementDto> schedule = new ArrayList<>();
        BigDecimal remainingDebt = amount;

        for (int i = 1; i <= term; i++) {
            //Рассчитываем % и основную часть платежа для каждого месяца
            BigDecimal interestPayment = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);

            //Если месяц последний, включаем оставшийся долг в последний платеж
            if (i == term) {
                debtPayment = debtPayment.add(remainingDebt).setScale(2, RoundingMode.HALF_DOWN);
                remainingDebt = BigDecimal.ZERO; // обнуляем остаток
            } else {
                remainingDebt = remainingDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);
            }

            //лог данных по каждому месяцу
            log.debug("Month {}: interestPayment = {}, debtPayment = {}, remainingDebt = {}", i, interestPayment, debtPayment, remainingDebt);


            //Создаем элемент для графика
            PaymentScheduleElementDto element = PaymentScheduleElementDto.builder()
                            .number(i)
                                    .date(LocalDate.now().plusMonths(i))
                                            .totalPayment(monthlyPayment)
                                                    .interestPayment(interestPayment)
                                                            .debtPayment(debtPayment)
                                                                    .remainingDebt(remainingDebt)
                                                                            .build();
            schedule.add(element);
        }
        log.debug("Payment schedule created: {}", schedule);
        return schedule;
    }
}
