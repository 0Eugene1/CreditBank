package com.example.calculator.factory;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.service.PrescoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "loan.base-rate=10.0")
public class LoanOfferFactoryTest {

    @Mock
    private PrescoringService prescoringService;

    @Autowired
    private LoanOfferFactory loanOfferFactory;

    private LoanStatementRequestDto validRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация стандартного валидного запроса
        validRequest = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .passportSeries("1234")
                .passportNumber("567890")
                .email("qweqweqwe@gmail.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .middleName("Olegov")
                .firstName("Oleg")
                .lastName("Olegovich")
                .build();

    }

    // Тестирование успешного создания предложения
    @Test
    void testCreateOffer_Success() {

        // Мокируем поведение prescoringService
        doNothing().when(prescoringService).validate(validRequest); // Мокаем метод validate, который ничего не возвращает

        // Вызываем метод
        LoanOfferDto offer = loanOfferFactory.createOffer(validRequest, 12, true, false);

        // Проверки
        assertNotNull(offer);
        // Ожидаем, что ставка после модификаций будет 7.0
        assertEquals(0, offer.getRate().compareTo(new BigDecimal("7.0"))); // Проверка ставки
    }


    // Тестирование с null в amount
    @Test
    void testCreateOffer_AmountIsNull() {
        validRequest.setAmount(null);

        // Мокируем, что prescoring прошёл
        doNothing().when(prescoringService).validate(validRequest);

        // Ожидаем выброс исключения
        assertThrows(IllegalArgumentException.class, () -> loanOfferFactory.createOffer(validRequest, 12, true, true));
    }

    // Тестирование с неверным значением term
    @Test
    void testCreateOffer_InvalidTerm() {
        validRequest.setTerm(0);

        // Мокируем, что prescoring прошёл
        doNothing().when(prescoringService).validate(validRequest);

        // Ожидаем выброс исключения
        assertThrows(IllegalArgumentException.class, () -> loanOfferFactory.createOffer(validRequest, 0, true, true));
    }

    // Тестирование с некорректной baseRate
    @Test
    void testCreateOffer_InvalidBaseRate() {

        // Мокируем, что prescoring прошёл
        doNothing().when(prescoringService).validate(validRequest);

        // Устанавливаем неправильную baseRate (например, 0)
        loanOfferFactory = new LoanOfferFactory(prescoringService);

        // Ожидаем выброс исключения
        assertThrows(NullPointerException.class, () -> loanOfferFactory.createOffer(validRequest, 12, true, true));
    }


}

