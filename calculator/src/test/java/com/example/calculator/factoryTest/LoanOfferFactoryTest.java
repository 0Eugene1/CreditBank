package com.example.calculator.factoryTest;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.factory.LoanOfferFactory;
import com.example.calculator.service.PrescoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("100000"));
        request.setTerm(12);
        request.setPassportSeries("1234");
        request.setPassportNumber("567890");
        request.setEmail("qweqweqwe@gmail.com");
        request.setBirthDate(LocalDate.of(1990,1,1));
        request.setMiddleName("Olegov");
        request.setFirstName("Oleg");
        request.setLastName("Olegovich");

        // Мокируем поведение prescoringService
        when(prescoringService.validate(request)).thenReturn(true);

        // Вызываем метод
        LoanOfferDto offer = loanOfferFactory.createOffer(request, 12, true, false);

        // Проверки
        assertNotNull(offer);
        // Ожидаем, что ставка после модификаций будет 7.0
        assertEquals(0, offer.getRate().compareTo(new BigDecimal("7.0"))); // Проверка ставки
    }

    // Тестирование с неправильным prescoring (не прошел проверку)
    @Test
    void testCreateOffer_FailsPrescoring() {
        // Подготовка данных для теста
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("100000"));
        request.setTerm(12);

        // Мокируем, что prescoring не прошёл
        when(prescoringService.validate(request)).thenReturn(false);

        // Ожидаем выброс исключения
        assertThrows(IllegalArgumentException.class, () -> {
            loanOfferFactory.createOffer(request, 12, true, true);
        });
    }

    // Тестирование с null в amount
    @Test
    void testCreateOffer_AmountIsNull() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(null);
        request.setTerm(12);

        // Мокируем, что prescoring прошёл
        when(prescoringService.validate(request)).thenReturn(true);

        // Ожидаем выброс исключения
        assertThrows(IllegalArgumentException.class, () -> {
            loanOfferFactory.createOffer(request, 12, true, true);
        });
    }

    // Тестирование с неверным значением term
    @Test
    void testCreateOffer_InvalidTerm() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("100000"));
        request.setTerm(0); // Неверный срок

        // Мокируем, что prescoring прошёл
        when(prescoringService.validate(request)).thenReturn(true);

        // Ожидаем выброс исключения
        assertThrows(IllegalArgumentException.class, () -> {
            loanOfferFactory.createOffer(request, 0, true, true);
        });
    }

    // Тестирование с некорректной baseRate
    @Test
    void testCreateOffer_InvalidBaseRate() {
        // Подготовка данных для теста
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("100000"));
        request.setTerm(12);

        // Мокируем, что prescoring прошёл
        when(prescoringService.validate(request)).thenReturn(true);

        // Устанавливаем неправильную baseRate (например, 0)
        loanOfferFactory = new LoanOfferFactory(prescoringService);

        // Ожидаем выброс исключения
        assertThrows(IllegalStateException.class, () -> {
            loanOfferFactory.createOffer(request, 12, true, true);
        });
    }


    //FIXME DOES NOT WORK

//    // Тестирование с корректным значением baseRate
//    @Test
//    void testCreateOffer_ValidBaseRate() {
//        LoanStatementRequestDto request = new LoanStatementRequestDto();
//        request.setAmount(new BigDecimal("100000"));
//        request.setTerm(12);
//        request.setPassportSeries("1234");
//        request.setPassportNumber("567890");
//        request.setEmail("qweqweqwe@gmail.com");
//        request.setBirthDate(LocalDate.of(1990,1,1));
//        request.setMiddleName("Olegov");
//        request.setFirstName("Oleg");
//        request.setLastName("Olegovich");
//
//
//        // Мокируем, что prescoring прошёл
//        when(prescoringService.validate(request)).thenReturn(true);
//
//        // Устанавливаем корректную baseRate
//        loanOfferFactory = new LoanOfferFactory(prescoringService);
//
//        LoanOfferDto offer = loanOfferFactory.createOffer(request, 12, true, false);
//
//        // Проверки
//        assertNotNull(offer);
//        assertEquals(new BigDecimal("100000"), offer.getRequestedAmount());
//        assertEquals(new BigDecimal("200000"), offer.getTotalAmount());
//        assertEquals(new BigDecimal("7"), offer.getRate());
//    }
}

