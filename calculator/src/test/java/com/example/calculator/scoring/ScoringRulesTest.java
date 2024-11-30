package com.example.calculator.scoring;

import com.example.calculator.dto.EmploymentDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.GenderEnum;
import com.example.calculator.enums.MaritalStatusEnum;
import com.example.calculator.enums.PositionEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ScoringRulesTest {

    @Test
    public void testApplyEmploymentStatus_Unemployed_ShouldThrowException() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatusEnum.UNEMPLOYED);
        data.setEmployment(employment);

        assertThrows(IllegalArgumentException.class, () -> {
            ScoringRules.applyEmploymentStatus(data, 5.0);
        });
    }

    @Test
    public void testApplyEmploymentStatus_SelfEmployed_ShouldIncreaseRateBy2() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatusEnum.SELF_EMPLOYED);
        data.setEmployment(employment);

        double rate = ScoringRules.applyEmploymentStatus(data, 5.0);
        assertEquals(7.0, rate);
    }

    @Test
    public void testApplyEmploymentStatus_BusinessOwner_ShouldIncreaseRateBy1() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatusEnum.BUSINESS_OWNER);
        data.setEmployment(employment);

        double rate = ScoringRules.applyEmploymentStatus(data, 5.0);
        assertEquals(6.0, rate);
    }

    @Test
    public void testApplyPositionStatus_MiddleManager_ShouldDecreaseRateBy2() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setPosition(PositionEnum.MIDDLE_MANAGER);
        data.setEmployment(employment);

        double rate = ScoringRules.applyPositionStatus(data, 5.0);
        assertEquals(3.0, rate);
    }

    @Test
    public void testApplyPositionStatus_TopManager_ShouldDecreaseRateBy3() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setPosition(PositionEnum.TOP_MANAGER);
        data.setEmployment(employment);

        double rate = ScoringRules.applyPositionStatus(data, 5.0);
        assertEquals(2.0, rate);
    }

    @Test
    public void testIsLoanAmountAcceptable_ShouldReturnTrue() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(BigDecimal.valueOf(30000)); // Зарплата
        data.setEmployment(employment);
        data.setAmount(BigDecimal.valueOf(50000)); // Сумма займа

        boolean result = ScoringRules.isLoanAmountAcceptable(data);
        assertTrue(result);
    }

    @Test
    public void testIsLoanAmountAcceptable_ShouldReturnFalse() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(BigDecimal.valueOf(30000)); // Зарплата
        data.setEmployment(employment);
        data.setAmount(BigDecimal.valueOf(1000000)); // Сумма займа слишком большая

        boolean result = ScoringRules.isLoanAmountAcceptable(data);
        assertFalse(result);
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
        data.setBirthDate(LocalDate.of(2010, 1, 1)); // Возраст 14 лет

        boolean result = ScoringRules.isAgeValid(data);
        assertFalse(result);
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

        boolean result = ScoringRules.isExperienceValid(data);
        assertTrue(result);
    }

    @Test
    public void testIsExperienceValid_ShouldReturnFalse() {
        ScoringDataDto data = new ScoringDataDto();
        EmploymentDto employment = new EmploymentDto();
        employment.setWorkExperienceTotal(10);
        employment.setWorkExperienceCurrent(2);
        data.setEmployment(employment);

        boolean result = ScoringRules.isExperienceValid(data);
        assertFalse(result);
    }
}