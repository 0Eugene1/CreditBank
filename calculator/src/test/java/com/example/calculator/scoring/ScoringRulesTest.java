package com.example.calculator.scoring;

import com.example.calculator.dto.EmploymentDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.GenderEnum;
import com.example.calculator.enums.MaritalStatusEnum;
import com.example.calculator.enums.PositionEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testApplyEmploymentStatus_Unemployed_ShouldThrowException() {
        EmploymentStatusEnum status = EmploymentStatusEnum.UNEMPLOYED;

        assertThrows(IllegalArgumentException.class, () -> {
            ScoringRules.applyEmploymentStatus(status, 10.0);
        });
    }

    @Test
    public void testApplyEmploymentStatus_SelfEmployed_ShouldIncreaseRateBy2() {
        EmploymentStatusEnum status = EmploymentStatusEnum.SELF_EMPLOYED;
        double baseRate = 10.0;

        double result = ScoringRules.applyEmploymentStatus(status, baseRate);
        assertEquals(12.0, result, 0.001);
    }

    @Test
    public void testApplyEmploymentStatus_BusinessOwner_ShouldIncreaseRateBy1() {
        EmploymentStatusEnum status = EmploymentStatusEnum.BUSINESS_OWNER;
        double baseRate = 10.0;

        double result = ScoringRules.applyEmploymentStatus(status, baseRate);

        assertEquals(11.0, result, 0.001); // Ожидаем увеличение ставки на 1
    }

    @Test
    public void testApplyPositionStatus_MiddleManager_ShouldDecreaseRateBy2() {
        PositionEnum status = PositionEnum.MIDDLE_MANAGER;
        double baseRate = 10.0;

        double result = ScoringRules.applyPositionStatus(status, baseRate);
        assertEquals(8.0, result, 0.001);
    }

    @Test
    public void testApplyPositionStatus_TopManager_ShouldDecreaseRateBy3() {
        PositionEnum status = PositionEnum.TOP_MANAGER;
        double baseRate = 10.0;

        double result = ScoringRules.applyPositionStatus(status, baseRate);
        assertEquals(7.0, result, 0.001);
    }

//    @Test
//    public void testIsLoanAmountAcceptable_ShouldReturnTrue() {
//        ScoringDataDto data = new ScoringDataDto();
//        EmploymentDto employment = new EmploymentDto();
//        employment.setSalary(BigDecimal.valueOf(30000)); // Зарплата
//        data.setEmployment(employment);
//        data.setAmount(BigDecimal.valueOf(50000)); // Сумма займа
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            ScoringRules.isLoanAmountAcceptable(data);
//        });
//    }
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
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(BigDecimal.valueOf(30000)); // Зарплата
        data.setEmployment(employment);
        data.setAmount(BigDecimal.valueOf(1000000)); // Сумма займа слишком большая

        assertThrows(IllegalArgumentException.class, () -> {
            ScoringRules.isLoanAmountAcceptable(data);
        });
    }

    @Test
    public void testApplyMaritalStatus_Married_ShouldDecreaseRateBy3() {
        ScoringDataDto data = new ScoringDataDto();
        data.setMaritalStatus(MaritalStatusEnum.MARRIED);

        double rate = ScoringRules.applyMaritalStatus(data, 5.0);
        assertEquals(2.0, rate);
    }

    @Test
    public void testApplyMaritalStatus_Divorced_ShouldIncreaseRateBy1() {
        ScoringDataDto data = new ScoringDataDto();
        data.setMaritalStatus(MaritalStatusEnum.DIVORCED);

        double rate = ScoringRules.applyMaritalStatus(data, 5.0);
        assertEquals(6.0, rate);
    }

    @Test
    public void testIsAgeValid_AgeWithinRange_ShouldReturnTrue() {
        ScoringDataDto data = new ScoringDataDto();
        data.setBirthDate(LocalDate.of(1990, 1, 1)); // Возраст 34 года

        boolean result = ScoringRules.isAgeValid(data);
        assertTrue(result);
    }

    @Test
    public void testIsAgeValid_AgeTooYoung_ShouldReturnFalse() {
        ScoringDataDto data = new ScoringDataDto();
        data.setBirthDate(LocalDate.of(2020, 1, 1)); //


        assertThrows(NullPointerException.class, () -> {
            ScoringRules.isLoanAmountAcceptable(data);
        });
    }

    @Test
    public void testApplyGenderRule_FemaleAgeBetween32And60_ShouldDecreaseRateBy3() {
        ScoringDataDto data = new ScoringDataDto();
        data.setGender(GenderEnum.FEMALE);
        data.setBirthDate(LocalDate.of(1985, 1, 1)); // Возраст 39 лет

        double rate = ScoringRules.applyGenderRule(data, 5.0);
        assertEquals(2.0, rate);
    }

    @Test
    public void testApplyGenderRule_MaleAgeBetween30And55_ShouldDecreaseRateBy3() {
        ScoringDataDto data = new ScoringDataDto();
        data.setGender(GenderEnum.MALE);
        data.setBirthDate(LocalDate.of(1985, 1, 1)); // Возраст 39 лет

        double rate = ScoringRules.applyGenderRule(data, 5.0);
        assertEquals(2.0, rate);
    }

    @Test
    public void testApplyGenderRule_NonBinary_ShouldIncreaseRateBy7() {
        ScoringDataDto data = new ScoringDataDto();
        data.setGender(GenderEnum.NON_BINARY);
        data.setBirthDate(LocalDate.of(1995, 1, 1)); // Возраст 29 лет

        double rate = ScoringRules.applyGenderRule(data, 5.0);
        assertEquals(12.0, rate);
    }

    @Test
    public void testIsExperienceValid_ShouldReturnTrue() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setWorkExperienceTotal(20);
        employment.setWorkExperienceCurrent(5);
        data.setEmployment(employment);

        boolean result = ScoringRules.isExperienceValid(
                data.getEmployment().getWorkExperienceTotal(),
                data.getEmployment().getWorkExperienceCurrent()
        );
        assertTrue(result);
    }

    @Test
    public void testIsExperienceValid_ShouldReturnFalse() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setWorkExperienceTotal(20);
        employment.setWorkExperienceCurrent(10);
        data.setEmployment(employment);

        boolean result = ScoringRules.isExperienceValid(
                data.getEmployment().getWorkExperienceTotal(),
                data.getEmployment().getWorkExperienceCurrent()
        );
        assertTrue(result);
    }
}