package com.example.calculator.offerServiceTest;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.factory.LoanOfferFactory;
import com.example.calculator.service.LoanOfferService;
import com.example.calculator.service.PrescoringService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class LoanOfferServiceTest {

    @Mock
    private LoanOfferFactory loanOfferFactory;

    @Mock
    private PrescoringService prescoringService;

    @InjectMocks
    private LoanOfferService loanOfferService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loanOfferService = new LoanOfferService(loanOfferFactory, prescoringService);
    }

    @Test
    void shouldCreateAndSortLoanOffersSuccessfully() {
        // Given: Валидный LoanStatementRequestDto
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("100000"));
        request.setTerm(12);

        // Мокируем поведение PrescoringService
        Mockito.when(prescoringService.validate(request)).thenReturn(true);

        // Мокируем поведение LoanOfferFactory
        LoanOfferDto offer1 = createMockOffer(new BigDecimal("5.5"), new BigDecimal("105000"));
        LoanOfferDto offer2 = createMockOffer(new BigDecimal("4.5"), new BigDecimal("104000"));
        LoanOfferDto offer3 = createMockOffer(new BigDecimal("3.5"), new BigDecimal("103000"));
        LoanOfferDto offer4 = createMockOffer(new BigDecimal("2.5"), new BigDecimal("102000"));

        Mockito.when(loanOfferFactory.createOffer(request, 12, false, false)).thenReturn(offer1);
        Mockito.when(loanOfferFactory.createOffer(request, 12, false, true)).thenReturn(offer2);
        Mockito.when(loanOfferFactory.createOffer(request, 12, true, false)).thenReturn(offer3);
        Mockito.when(loanOfferFactory.createOffer(request, 12, true, true)).thenReturn(offer4);

        // When: Вызываем calculateLoanOffers
        List<LoanOfferDto> result = loanOfferService.calculateLoanOffers(request);

        // Then: Проверяем результат
        Assertions.assertEquals(4, result.size(), "Should return 4 loan offers");
        Assertions.assertEquals(new BigDecimal("2.5"), result.get(0).getRate(), "First offer should have the lowest rate");
        Assertions.assertEquals(new BigDecimal("5.5"), result.get(3).getRate(), "Last offer should have the highest rate");

        // Проверяем порядок вызова методов фабрики
        Mockito.verify(loanOfferFactory).createOffer(request, 12, false, false);
        Mockito.verify(loanOfferFactory).createOffer(request, 12, false, true);
        Mockito.verify(loanOfferFactory).createOffer(request, 12, true, false);
        Mockito.verify(loanOfferFactory).createOffer(request, 12, true, true);
    }

    // Вспомогательный метод для создания LoanOfferDto
    private LoanOfferDto createMockOffer(BigDecimal rate, BigDecimal totalAmount) {
        LoanOfferDto offer = new LoanOfferDto();
        offer.setRate(rate);
        offer.setTotalAmount(totalAmount);
        return offer;
    }
    @Test
    public void calculateLoanOffers_prescoringFails() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        when(prescoringService.validate(request)).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            loanOfferService.calculateLoanOffers(request);
        });
        assertEquals("Предварительная проверка заявки не пройдена", thrown.getMessage());
    }

    @Test
    void testCalculateLoanOffers_Success() {
        // Создаем объект запроса для теста
        LoanStatementRequestDto mockRequest = new LoanStatementRequestDto();
        mockRequest.setAmount(new BigDecimal("10000"));
        mockRequest.setTerm(12);
        mockRequest.setFirstName("John");
        mockRequest.setLastName("Doe");
        mockRequest.setEmail("john.doe@example.com");
        mockRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        mockRequest.setPassportSeries("ABCD");
        mockRequest.setPassportNumber("123456");

        // Мокируем метод validate, чтобы он возвращал true
        when(prescoringService.validate(any(LoanStatementRequestDto.class))).thenReturn(true);

        // Мокаем метод createOffer для возврата стандартных значений
        when(loanOfferFactory.createOffer(any(LoanStatementRequestDto.class), anyInt(), anyBoolean(), anyBoolean()))
                .thenAnswer(invocation -> {
                    LoanOfferDto offer = new LoanOfferDto();
                    offer.setRate(new BigDecimal("10.0"));
                    offer.setMonthlyPayment(new BigDecimal("1000"));
                    offer.setRequestedAmount(new BigDecimal("10000"));
                    offer.setTotalAmount(new BigDecimal("12000"));
                    offer.setTerm(12);
                    offer.setIsInsuranceEnabled(false);
                    offer.setIsSalaryClient(false);
                    return offer;
                });

        // Выполняем расчет
        List<LoanOfferDto> offers = loanOfferService.calculateLoanOffers(mockRequest);

        // Печатаем offers для отладки
        offers.forEach(offer -> System.out.println("Offer: " + offer));

        // Проверяем, что список не пуст
        assertFalse(offers.isEmpty(), "List of loan offers should not be empty");

        // Проверяем, что список содержит 4 предложения
        assertEquals(4, offers.size(), "There should be 4 loan offers");

        // Проверяем, что каждое предложение корректно
        for (LoanOfferDto offer : offers) {
            assertNotNull(offer, "Loan offer should not be null");
            assertNotNull(offer.getRate(), "Loan offer rate should not be null");
            assertNotNull(offer.getMonthlyPayment(), "Loan offer monthly payment should not be null");
            assertTrue(offer.getRate().compareTo(BigDecimal.ZERO) > 0, "Loan offer rate should be greater than zero");
            assertTrue(offer.getRequestedAmount().compareTo(BigDecimal.ZERO) > 0, "Requested amount should be greater than zero");
            assertTrue(offer.getTotalAmount().compareTo(BigDecimal.ZERO) > 0, "Total amount should be greater than zero");

            // Проверяем вызовы createOffer с правильными параметрами
            verify(loanOfferFactory, times(4)).createOffer(any(), anyInt(), anyBoolean(), anyBoolean());
        }

        }

        @Test
        void testCalculateLoanOffers_PrescoringFails() {
            // Подготовка данных
            LoanStatementRequestDto request = new LoanStatementRequestDto();
            request.setAmount(new BigDecimal("100000"));
            request.setTerm(12);
            request.setFirstName("John");
            request.setLastName("Doe");
            request.setMiddleName("Aev");
            request.setEmail("john.doe@example.com");
            request.setBirthDate(LocalDate.of(1990, 1, 1));
            request.setPassportNumber("567890");
            request.setPassportSeries("1234");

            // Мокируем поведение prescoringService так, чтобы валидация не прошла
            when(prescoringService.validate(request)).thenReturn(false);

            // Вызов тестируемого метода и проверка на то, что выбрасывается исключение
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loanOfferService.calculateLoanOffers(request));

            // Проверка на правильное сообщение исключения
            assertEquals("Предварительная проверка заявки не пройдена", exception.getMessage());

            // Проверка, что метод фабрики не был вызван
            verify(loanOfferFactory, never()).createOffer(any(), anyInt(), anyBoolean(), anyBoolean());
        }
//    //FIXME
//
//    @Test
//    void calculateLoanOffers_shouldReturnValidOffers() {
//        // Подготовка данных
//        LoanStatementRequestDto request = new LoanStatementRequestDto();
//        request.setAmount(new BigDecimal("100000"));
//        request.setTerm(12);
//        request.setFirstName("John");
//        request.setLastName("Doe");
//        request.setMiddleName("Aev");
//        request.setEmail("john.doe@example.com");
//        request.setBirthDate(LocalDate.of(1990, 1, 1));
//        request.setPassportNumber("567890");
//        request.setPassportSeries("1234");
//
//
//        // Мокирование PrescoringService
//        when(prescoringService.validate(request)).thenReturn(true);  // Убедитесь, что prescoring возвращает true для продолжения
//
//        // Мокирование LoanOfferFactory
//        LoanOfferDto mockOffer1 = mock(LoanOfferDto.class);
//        LoanOfferDto mockOffer2 = mock(LoanOfferDto.class);
//        LoanOfferDto mockOffer3 = mock(LoanOfferDto.class);
//        LoanOfferDto mockOffer4 = mock(LoanOfferDto.class);
//
//        // Настройка мока на возврат предложений
//        when(loanOfferFactory.createOffer(request, 12, false, false)).thenReturn(mockOffer1);
//        when(loanOfferFactory.createOffer(request, 12, true, false)).thenReturn(mockOffer2);
//        when(loanOfferFactory.createOffer(request, 12, true, true)).thenReturn(mockOffer3);
//        when(loanOfferFactory.createOffer(request, 12, false, true)).thenReturn(mockOffer4);
//
//        // Вызов метода
//        List<LoanOfferDto> offers = loanOfferService.calculateLoanOffers(request);
//
//        // Логирование для диагностики
//        System.out.println("Offers size after calculation: " + offers.size());
//        offers.forEach(offer -> System.out.println("Offer: " + offer));
//
//        // Проверка
//        assertNotNull(offers, "Offer list should not be null");
//        assertEquals(4, offers.size(), "There should be 4 valid loan offers");
//
//        // Проверяем, что метод validate был вызван один раз
//        verify(prescoringService, times(1)).validate(request);
//
//        // Проверяем, что метод createOffer был вызван 4 раза с правильными параметрами
//        verify(loanOfferFactory, times(1)).createOffer(request, 12, false, false);
//        verify(loanOfferFactory, times(1)).createOffer(request, 12, true, false);
//        verify(loanOfferFactory, times(1)).createOffer(request, 12, true, true);
//        verify(loanOfferFactory, times(1)).createOffer(request, 12, false, true);
//
//        // Проверка, что возвращенные предложения не содержат null или пустых rate
//        for (LoanOfferDto offer : offers) {
//            assertNotNull(offer, "Offer should not be null");
//            assertNotNull(offer.getRate(), "Rate should not be null");
//        }

    @Test
    void calculateLoanOffers_shouldThrowExceptionWhenPrescoringFails() {
        // Подготовка данных
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("100000"));
        request.setTerm(12);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setMiddleName("Aev");
        request.setEmail("john.doe@example.com");
        request.setBirthDate(LocalDate.of(1990,1,1));
        request.setPassportNumber("567890");
        request.setPassportSeries("1234");
        // Мокирование PrescoringService
        when(prescoringService.validate(request)).thenReturn(false);

        // Вызов метода и проверка на исключение
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanOfferService.calculateLoanOffers(request);
        });

        assertEquals("Предварительная проверка заявки не пройдена", exception.getMessage());
    }

}
