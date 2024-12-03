package com.example.calculator.service;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.factory.LoanOfferFactory;
import com.example.calculator.service.LoanOfferService;
import com.example.calculator.service.PrescoringService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest
@AutoConfigureMockMvc
public class LoanOfferServiceTest {

    @Autowired
    private MockMvc mockMvc;

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
    // Валидный LoanStatementRequestDto
    LoanStatementRequestDto request = LoanStatementRequestDto.builder()
            .amount(new BigDecimal("100000"))
            .term(12)
            .build();

    // Мокируем поведение PrescoringService
    Mockito.doNothing().when(prescoringService).validate(request);

    // Мокируем поведение LoanOfferFactory
    LoanOfferDto offer1 = createMockOffer(new BigDecimal("10.0"), new BigDecimal("105000"));
    LoanOfferDto offer2 = createMockOffer(new BigDecimal("4.5"), new BigDecimal("104000"));
    LoanOfferDto offer3 = createMockOffer(new BigDecimal("3.5"), new BigDecimal("103000"));
    LoanOfferDto offer4 = createMockOffer(new BigDecimal("2.5"), new BigDecimal("102000"));

    // Мокируем createOffers, чтобы возвращался список предложений
    Mockito.when(loanOfferFactory.createOffers(Mockito.any(), Mockito.anyInt()))
            .thenReturn(new ArrayList<>(List.of(offer1, offer2, offer3, offer4))); // Используем изменяемый список

    // Вызываем calculateLoanOffers
    List<LoanOfferDto> result = loanOfferService.calculateLoanOffers(request);

    // Проверяем, что результат не пустой
    Assertions.assertNotNull(result, "Result should not be null");
    Assertions.assertFalse(result.isEmpty(), "Result list should not be empty");

    // Проверяем, что возвращено 4 предложения
    Assertions.assertEquals(4, result.size(), "Should return 4 loan offers");

    // Проверяем, что предложения отсортированы по ставке
    Assertions.assertEquals(new BigDecimal("2.5"), result.get(0).getRate(), "First offer should have the lowest rate");
    Assertions.assertEquals(new BigDecimal("10.0"), result.get(3).getRate(), "Last offer should have the highest rate");

    // Проверяем порядок вызова методов фабрики
    Mockito.verify(loanOfferFactory).createOffers(Mockito.any(), Mockito.anyInt());
}



    // Вспомогательный метод для создания LoanOfferDto
    private LoanOfferDto createMockOffer(BigDecimal rate, BigDecimal totalAmount) {
        LoanOfferDto offer = new LoanOfferDto();
        offer.setRate(rate);
        offer.setTotalAmount(totalAmount);
        return offer;
    }

    @Test
    void testCalculateLoanOffers_Success() {
        // Подготовка данных
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Aev")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        LoanOfferDto offer1 = new LoanOfferDto();
        offer1.setRate(new BigDecimal("5.0"));
        LoanOfferDto offer2 = new LoanOfferDto();
        offer2.setRate(new BigDecimal("3.5"));
        LoanOfferDto offer3 = new LoanOfferDto();
        offer3.setRate(new BigDecimal("4.2"));

        // Мокирование зависимости
        when(loanOfferFactory.createOffers(request, request.getTerm()))
                .thenReturn(Arrays.asList(offer1, offer2, offer3));

        // Вызов тестируемого метода
        List<LoanOfferDto> result = loanOfferService.calculateLoanOffers(request);

        // Проверка
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.get(0).getRate().compareTo(result.get(1).getRate()) <= 0);
        assertTrue(result.get(1).getRate().compareTo(result.get(2).getRate()) <= 0);
    }

    @Test
    void testCalculateLoanOffers_NoOffersFromFactory() {
        // Подготовка данных
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Aev")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        // Мокирование зависимости, фабрика возвращает пустой список
        when(loanOfferFactory.createOffers(request, request.getTerm()))
                .thenReturn(Collections.emptyList());

        // Вызов тестируемого метода
        List<LoanOfferDto> result = loanOfferService.calculateLoanOffers(request);

        // Проверка
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateLoanOffers_FactoryException() {
        // Подготовка данных
        LoanStatementRequestDto request = LoanStatementRequestDto.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .firstName("John")
                .lastName("Doe")
                .middleName("Aev")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 1, 1))
                .passportSeries("1234")
                .passportNumber("567890")
                .build();

        // Мокирование фабрики, чтобы она выбросила исключение
        when(loanOfferFactory.createOffers(request, request.getTerm()))
                .thenThrow(new RuntimeException("Error creating loan offers"));

        // Вызов тестируемого метода и проверка на исключение
        RuntimeException exception = assertThrows(RuntimeException.class, () -> loanOfferService.calculateLoanOffers(request));
        assertEquals("Error creating loan offers", exception.getMessage());
    }

    @Test
    void testCalculateCredit_shouldReturnBadRequest_whenValidationFails() throws Exception {
        // Подготовка запроса с некорректными данными, которые вызовут ошибку валидации
        String invalidRequest = "{\"amount\": 50000, \"term\": 12, \"firstName\": \"John\", \"lastName\": \"Doe\"}"; // Отсутствуют обязательные поля, например, passportNumber, passportSeries, birthDate

        // Отправляем запрос, который должен вызвать ошибку валидации
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/calculator/calc")  // Путь к контроллеру
                        .contentType(MediaType.APPLICATION_JSON)  // Указание типа контента
                        .content(invalidRequest))  // Некорректные данные
                .andExpect(status().isBadRequest())  // Ожидаем статус 400
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(org.hamcrest.Matchers.containsString("passportNumber: Номер паспорта не может быть пустым"))) // Проверка ошибки
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(org.hamcrest.Matchers.containsString("passportSeries: Серия паспорта не может быть пустой"))) // Проверка ошибки
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(org.hamcrest.Matchers.containsString("birthDate: Поле минимальный возраст не может быть пустым")))  // Проверка ошибки
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));  // Проверка статуса
    }



    @Test
    void testCalculateLoanOffers_NullPointerException() throws Exception {
        // Подготовка запроса с пустым телом
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/calculator/offers")  // Путь к контроллеру
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))  // Пустое тело запроса
                .andExpect(status().isBadRequest())  // Ожидаемый статус 400
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Некорректное или отсутствующее тело запроса"));  // Проверка ошибки
    }

}
