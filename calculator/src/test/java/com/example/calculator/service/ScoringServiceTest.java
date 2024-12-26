package com.example.calculator.service;

import com.example.calculator.dto.EmploymentDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.GenderEnum;
import com.example.calculator.enums.MaritalStatusEnum;
import com.example.calculator.enums.PositionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "loan.base-rate=10.0")
public class ScoringServiceTest {

    @Value("${loan.base-rate}")
    private BigDecimal baseRate;

    @Mock
    private PrescoringService prescoringServiceMock;

    @Autowired
    private ScoringService scoringServiceToTest;

    private ScoringDataDto validData;
    private ScoringDataDto invalidData;
    private EmploymentDto invalidEmployment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Инициализация общих данных для тестов
        EmploymentDto validEmployment = EmploymentDto.builder()
                .workExperienceTotal(20)
                .workExperienceCurrent(15)
                .salary(BigDecimal.valueOf(30000))
                .employerINN("123456")
                .position(PositionEnum.MIDDLE_MANAGER)
                .employmentStatus(EmploymentStatusEnum.BUSINESS_OWNER)
                .build();

        validData = ScoringDataDto.builder()
                .birthDate(LocalDate.now().minusYears(20))
                .term(12)
                .amount(BigDecimal.valueOf(50000))
                .isInsuranceEnabled(true)
                .firstName("Oleg")
                .lastName("Olegov")
                .middleName("Olegovich")
                .isSalaryClient(true)
                .maritalStatus(MaritalStatusEnum.DIVORCED)
                .gender(GenderEnum.MALE)
                .employment(validEmployment)  // Используем employment с builder
                .build();

        invalidEmployment = EmploymentDto.builder()
                .workExperienceTotal(5)
                .workExperienceCurrent(2)
                .salary(BigDecimal.valueOf(30000))
                .employerINN("123456")
                .position(PositionEnum.MIDDLE_MANAGER)
                .employmentStatus(EmploymentStatusEnum.SELF_EMPLOYED)
                .build();

        invalidData = ScoringDataDto.builder()
                .birthDate(LocalDate.now().minusYears(15))
                .amount(BigDecimal.valueOf(50000))
                .term(12)
                .gender(GenderEnum.MALE)
                .maritalStatus(MaritalStatusEnum.SINGLE)
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .employment(invalidEmployment)  // Используем employment с builder
                .build();
    }

    @Test
    public void testCalculateRate_validData() {
        // Мокирование возвращаемого значения для baseRate
        doNothing().when(prescoringServiceMock).validate(validData);

        // Проверка значения baseRate через сравнение BigDecimal
        BigDecimal calculatedRate = scoringServiceToTest.calculateRate(validData);
        assertEquals(0, calculatedRate.compareTo(baseRate), "Calculated rate should match baseRate");
    }


    @Test
    public void testInvalidAge() {
        // Проверяем, что выбрасывается IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> scoringServiceToTest.calculateRate(invalidData));
    }

    @Test
    public void testInvalidExperience() {
        // Создаем EmploymentDto с некорректным стажем
        invalidEmployment.setWorkExperienceTotal(1); // Некорректный общий стаж
        invalidEmployment.setWorkExperienceCurrent(0); // Некорректный текущий стаж

        // Проверяем, что выбрасывается исключение IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> scoringServiceToTest.calculateRate(invalidData));

        // Убеждаемся, что текст исключения правильный
        assertEquals("Отказ по стажу: недостаточный стаж.", exception.getMessage());
    }

    @Test
    public void testCalculateRate_invalidLoanAmount() {
        ScoringDataDto invalidLoanAmountData = ScoringDataDto.builder()
                .birthDate(LocalDate.now().minusYears(25))
                .term(12)
                .amount(BigDecimal.valueOf(250000))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .maritalStatus(MaritalStatusEnum.SINGLE)
                .gender(GenderEnum.MALE)
                .employment(EmploymentDto.builder()
                        .workExperienceTotal(5)
                        .workExperienceCurrent(2)
                        .salary(BigDecimal.valueOf(10000))
                        .employerINN("123456789")
                        .position(PositionEnum.MIDDLE_MANAGER)
                        .employmentStatus(EmploymentStatusEnum.SELF_EMPLOYED)
                        .build())  // Используем employment с builder
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> scoringServiceToTest.calculateRate(invalidLoanAmountData));

        assertEquals("Отказ по стажу: недостаточный стаж.", exception.getMessage());
    }
}
