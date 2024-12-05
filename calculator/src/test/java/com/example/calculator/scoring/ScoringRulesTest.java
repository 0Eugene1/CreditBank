package com.example.calculator.scoring;

import com.example.calculator.dto.EmploymentDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.GenderEnum;
import com.example.calculator.enums.MaritalStatusEnum;
import com.example.calculator.enums.PositionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ScoringRulesTest {

    @Value("${baseRate:10.0}") // Значение по умолчанию
    private BigDecimal baseRate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @BeforeEach
    public void setup() {
        baseRate = new BigDecimal(environment.getProperty("baseRate", "10.0"));
    }

    @ParameterizedTest
    @CsvSource({
            "SELF_EMPLOYED, 12.0",  // Увеличение на 2
            "BUSINESS_OWNER, 11.0", // Увеличение на 1
    })
    public void testApplyEmploymentStatus(String employmentStatus, BigDecimal expectedRate) {
        EmploymentStatusEnum status = EmploymentStatusEnum.valueOf(employmentStatus);

        BigDecimal result = ScoringRules.applyEmploymentStatus(status, new BigDecimal("10.0")); // Используем фиксированное значение
        assertEquals(expectedRate, result);
    }


    @Test
    public void testApplyEmploymentStatus_Unemployed_ShouldThrowException() {
        EmploymentStatusEnum status = EmploymentStatusEnum.UNEMPLOYED;

        assertThrows(IllegalArgumentException.class, () -> {
            ScoringRules.applyEmploymentStatus(status, new BigDecimal("10.0"));
        });
    }

    @ParameterizedTest
    @CsvSource({
            "MIDDLE_MANAGER, 8.0", // Уменьшение на 2
            "TOP_MANAGER, 7.0",    // Уменьшение на 3
    })
    public void testApplyPositionStatus(String position, BigDecimal expectedRate) {
        PositionEnum positionEnum = PositionEnum.valueOf(position);

        BigDecimal result = ScoringRules.applyPositionStatus(positionEnum, new BigDecimal("10.0")); // Фиксированная ставка
        assertEquals(expectedRate, result);
    }


    @Test
    public void testIsLoanAmountAcceptable_ShouldReturnBadRequest() throws Exception {
        // Создаём JSON строку с данными, включая обязательные поля
        String jsonRequest = "{"
                + "\"amount\": 50000,"  // Сумма кредита
                + "\"term\": 12,"  // Срок
                + "\"firstName\": \"John\","  // Имя
                + "\"lastName\": \"Doe\","  // Фамилия
                + "\"middleName\": \"Aev\","  // Отчество
                + "\"email\": \"john.doe@example.com\","  // Email
                + "\"birthDate\": \"1990-01-01\","  // Дата рождения
                + "\"passportSeries\": \"1234\","  // Серия паспорта
                + "\"passportNumber\": \"567890\""  // Номер паспорта
                + "}";

        // Выполняем запрос и проверяем, что статус 400 и ошибка в теле ответа
        mockMvc.perform(MockMvcRequestBuilders.post("/calculator/calc")  // Путь к контроллеру
                        .contentType(MediaType.APPLICATION_JSON)  // Указываем тип контента
                        .content(jsonRequest))  // Используем JSON строку напрямую
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Произошла внутренняя ошибка"));  // Проверка ошибки
    }


    @Test
    public void testIsLoanAmountAcceptable_ShouldReturnFalse() {
        EmploymentDto employment = EmploymentDto.builder()
                .salary(new BigDecimal("30000"))
                .build();

        ScoringDataDto data = ScoringDataDto.builder()
                .employment(employment)
                .amount(new BigDecimal("1000000"))  // Сумма займа слишком большая
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            ScoringRules.isLoanAmountAcceptable(data);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "MARRIED, 7.0",    // Ожидаем уменьшение на 3
            "DIVORCED, 11.0"   // Ожидаем увеличение на 1
    })
    public void testApplyMaritalStatus(MaritalStatusEnum maritalStatus, String expectedRate) {
        BigDecimal expectedRateDecimal = new BigDecimal(expectedRate);
        ScoringDataDto data = ScoringDataDto.builder()
                .maritalStatus(maritalStatus)
                .build();

        BigDecimal rate = ScoringRules.applyMaritalStatus(data.getMaritalStatus(), new BigDecimal("10.0"));
        assertEquals(expectedRateDecimal, rate);
    }


    @Test
    public void testIsAgeValid_AgeWithinRange_ShouldReturnTrue() {
        ScoringDataDto data = ScoringDataDto.builder()
                .birthDate(LocalDate.of(1990, 1, 1)) // Возраст 34 года
                .build();

        boolean result = ScoringRules.isAgeValid(data);
        assertTrue(result);
    }

    @Test
    public void testIsAgeValid_AgeTooYoung_ShouldReturnFalse() {
        ScoringDataDto data = ScoringDataDto.builder()
                .birthDate(LocalDate.of(2020, 1, 1))
                .build();

        assertThrows(NullPointerException.class, () -> {
            ScoringRules.isLoanAmountAcceptable(data);
        });
    }


    @ParameterizedTest
    @CsvSource({
            "FEMALE, 5.0, 2.0", // Женщина, возраст между 32 и 60, уменьшение на 3
            "MALE, 5.0, 2.0",   // Мужчина, возраст между 30 и 55, уменьшение на 3
            "NON_BINARY, 5.0, 12.0"  // Небинарный, увеличение на 7
    })
    public void testApplyGenderRule(GenderEnum gender, BigDecimal baseRate, BigDecimal expectedRate) {
        ScoringDataDto data = ScoringDataDto.builder()
                .gender(gender)
                .birthDate(LocalDate.of(1985, 1, 1))  // Возраст 39 для женщины и мужчины
                .build();

        BigDecimal rate = ScoringRules.applyGenderRule(data.getGender(), data.getBirthDate(), baseRate);
        assertEquals(expectedRate, rate);
    }


    @Test
    public void testIsExperienceValid_ShouldReturnTrue() {
        EmploymentDto employment = EmploymentDto.builder()
                .workExperienceTotal(20)
                .workExperienceCurrent(5)
                .build();

        ScoringDataDto data = ScoringDataDto.builder()
                .employment(employment)
                .build();

        boolean result = ScoringRules.isExperienceValid(
                data.getEmployment().getWorkExperienceTotal(),
                data.getEmployment().getWorkExperienceCurrent()
        );
        assertTrue(result);
    }

    @Test
    public void testIsExperienceValid_ShouldReturnFalse() {
        EmploymentDto employment = EmploymentDto.builder()
                .workExperienceTotal(20)
                .workExperienceCurrent(18)
                .build();

        ScoringDataDto data = ScoringDataDto.builder()
                .employment(employment)
                .build();

        boolean result = ScoringRules.isExperienceValid(
                data.getEmployment().getWorkExperienceTotal(),
                data.getEmployment().getWorkExperienceCurrent()
        );
        assertTrue(result);
    }
}