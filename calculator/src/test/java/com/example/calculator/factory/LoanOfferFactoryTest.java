package com.example.calculator.factory;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.exception.CalculatorError;
import com.example.calculator.service.PrescoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "loan.base-rate=10.0")
public class LoanOfferFactoryTest {

    @Mock
    private PrescoringService prescoringService;

    @Autowired
    private LoanOfferFactory loanOfferFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    // Тестирование успешного создания предложения
    @Test
    void testCreateOffer_Success() {
        // Подготовка данных для теста
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
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
        // Мокируем поведение prescoringService
        doNothing().when(prescoringService).validate(request); // Мокаем метод validate, который ничего не возвращает

        // Вызываем метод
        LoanOfferDto offer = loanOfferFactory.createOffer(request, 12, true, false);

        // Проверки
        assertNotNull(offer);
        // Ожидаем, что ставка после модификаций будет 7.0
        assertEquals(0, offer.getRate().compareTo(new BigDecimal("7.0"))); // Проверка ставки
    }


    // Тестирование с null в amount
    @Test
    void testCreateOffer_AmountIsNull() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(null)
                .term(12)
                .build();

        // Мокируем, что prescoring прошёл
        doNothing().when(prescoringService).validate(request);

        // Ожидаем выброс исключения
        assertThrows(IllegalArgumentException.class, () -> {
            loanOfferFactory.createOffer(request, 12, true, true);
        });
    }

    // Тестирование с неверным значением term
    @Test
    void testCreateOffer_InvalidTerm() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(0)
                .build();

        // Мокируем, что prescoring прошёл
        doNothing().when(prescoringService).validate(request);

        // Ожидаем выброс исключения
        assertThrows(IllegalArgumentException.class, () -> {
            loanOfferFactory.createOffer(request, 0, true, true);
        });
    }

    // Тестирование с некорректной baseRate
    @Test
    void testCreateOffer_InvalidBaseRate() {
        // Подготовка данных для теста
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .build();

        // Мокируем, что prescoring прошёл
        doNothing().when(prescoringService).validate(request);

        // Устанавливаем неправильную baseRate (например, 0)
        loanOfferFactory = new LoanOfferFactory(prescoringService);

        // Ожидаем выброс исключения
        assertThrows(IllegalStateException.class, () -> {
            loanOfferFactory.createOffer(request, 12, true, true);
        });
    }


}

