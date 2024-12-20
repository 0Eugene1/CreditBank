package com.example.calculator.service;

import com.example.calculator.dto.LoanOfferDto;
import com.example.calculator.dto.LoanStatementRequestDto;
import com.example.calculator.factory.LoanOfferFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

    // Глобальные переменные для часто используемых объектов
    private LoanStatementRequestDto validRequest;
    private LoanOfferDto offer1;
    private LoanOfferDto offer2;
    private LoanOfferDto offer3;
    private LoanOfferDto offer4;

    @BeforeEach
    public void setUp() {

        // Инициализация глобальных объектов
        validRequest = LoanStatementRequestDto.builder()
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

        offer1 = LoanOfferDto.builder()
                .rate(new BigDecimal("5.0"))
                .totalAmount(new BigDecimal("105000"))
                .build();

        offer2 = LoanOfferDto.builder()
                .rate(new BigDecimal("3.5"))
                .totalAmount(new BigDecimal("103500"))
                .build();

        offer3 = LoanOfferDto.builder()
                .rate(new BigDecimal("4.2"))
                .totalAmount(new BigDecimal("104200"))
                .build();

        offer4 = LoanOfferDto.builder()
                .rate(new BigDecimal("2.5"))
                .totalAmount(new BigDecimal("102500"))
                .build();


        MockitoAnnotations.openMocks(this);
        loanOfferService = new LoanOfferService(loanOfferFactory, prescoringService);
    }

    @Test
    void shouldCreateAndSortLoanOffersSuccessfully() {

        // Мокируем поведение PrescoringService
        Mockito.doNothing().when(prescoringService).validate(validRequest);

        // Мокируем createOffers, чтобы возвращался список предложений
        Mockito.when(loanOfferFactory.createOffers(Mockito.any(), Mockito.anyInt()))
                .thenReturn(new ArrayList<>(List.of(offer1, offer2, offer3, offer4))); // Используем изменяемый список

        // Вызываем calculateLoanOffers
        List<LoanOfferDto> result = loanOfferService.calculateLoanOffers(validRequest);

        // Проверяем, что результат не пустой
        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertFalse(result.isEmpty(), "Result list should not be empty");

        // Проверяем, что возвращено 4 предложения
        Assertions.assertEquals(4, result.size(), "Should return 4 loan offers");

        // Проверяем, что предложения отсортированы по ставке
        Assertions.assertEquals(new BigDecimal("2.5"), result.get(0).getRate(), "First offer should have the lowest rate");
        Assertions.assertEquals(new BigDecimal("5.0"), result.get(3).getRate(), "Last offer should have the highest rate");

        // Проверяем порядок вызова методов фабрики
        Mockito.verify(loanOfferFactory).createOffers(Mockito.any(), Mockito.anyInt());
    }


    @Test
    void testCalculateLoanOffers_Success() {

        // Мокирование зависимости
        when(loanOfferFactory.createOffers(validRequest, validRequest.getTerm()))
                .thenReturn(Arrays.asList(offer1, offer2, offer3));

        // Вызов тестируемого метода
        List<LoanOfferDto> result = loanOfferService.calculateLoanOffers(validRequest);

        // Проверка
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.get(0).getRate().compareTo(result.get(1).getRate()) <= 0);
        assertTrue(result.get(1).getRate().compareTo(result.get(2).getRate()) <= 0);
    }

    @Test
    void testCalculateLoanOffers_NoOffersFromFactory() {

        // Мокирование зависимости, фабрика возвращает пустой список
        when(loanOfferFactory.createOffers(validRequest, validRequest.getTerm()))
                .thenReturn(Collections.emptyList());

        // Вызов тестируемого метода
        List<LoanOfferDto> result = loanOfferService.calculateLoanOffers(validRequest);

        // Проверка
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateLoanOffers_FactoryException() {

        // Мокирование фабрики, чтобы она выбросила исключение
        when(loanOfferFactory.createOffers(validRequest, validRequest.getTerm()))
                .thenThrow(new RuntimeException("Error creating loan offers"));

        // Вызов тестируемого метода и проверка на исключение
        RuntimeException exception = assertThrows(RuntimeException.class, () -> loanOfferService.calculateLoanOffers(validRequest));
        assertEquals("Error creating loan offers", exception.getMessage());
    }

    @Test
    void testCalculateCredit_shouldReturnInternalServerError_whenUnexpectedErrorOccurs() throws Exception {
        // Подготовка запроса с данными, которые вызовут ошибку на сервере (например, null в обязательном поле)
        String invalidRequest = "{\"amount\": 50000, \"term\": 12, \"firstName\": \"John\", \"lastName\": \"Doe\", \"employment\": null}"; // Некорректные данные

        // Отправляем запрос, который должен вызвать ошибку на сервере
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/calculator/calc")  // Путь к контроллеру
                        .contentType(MediaType.APPLICATION_JSON)  // Указание типа контента
                        .content(invalidRequest))  // Некорректные данные
                .andExpect(status().isInternalServerError())  // Ожидаем статус 500
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Произошла внутренняя ошибка")))  // Проверка сообщения об ошибке
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(500));  // Проверка статуса 500
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
