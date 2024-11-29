package com.example.calculator.scoringTest;

import com.example.calculator.dto.EmploymentDto;
import com.example.calculator.dto.ScoringDataDto;
import com.example.calculator.enums.EmploymentStatusEnum;
import com.example.calculator.enums.GenderEnum;
import com.example.calculator.enums.MaritalStatusEnum;
import com.example.calculator.enums.PositionEnum;
import com.example.calculator.service.PrescoringService;
import com.example.calculator.service.ScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.assertThrows;


import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "loan.base-rate=10.0")
public class ScoringServiceTest {

    @Mock
    private PrescoringService prescoringServiceMock;

    @Autowired
    private ScoringService scoringServiceToTest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    //FIXME
    @Test
    public void testCalculateRate_validData() {
        // Создание валидных данных
        ScoringDataDto validData = new ScoringDataDto();
        validData.setBirthDate(LocalDate.now().minusYears(20));
        validData.setTerm(12);
        validData.setAmount(BigDecimal.valueOf(50000));
        validData.setIsInsuranceEnabled(true);
        validData.setFirstName("Oleg");
        validData.setLastName("Olegov");
        validData.setMiddleName("Olegovich");
        validData.setIsSalaryClient(true);
        validData.setMaritalStatus(MaritalStatusEnum.DIVORCED);
        validData.setGender(GenderEnum.MALE);

        // Инициализация EmploymentDto и установка его в ScoringDataDto
        EmploymentDto employmentDto = new EmploymentDto();
        employmentDto.setWorkExperienceTotal(20); // Установи требуемые значения
        employmentDto.setWorkExperienceCurrent(15);
        employmentDto.setSalary(BigDecimal.valueOf(30000));
        employmentDto.setEmployerINN("123456");
        employmentDto.setPosition(PositionEnum.MIDDLE_MANAGER);
        employmentDto.setEmploymentStatus(EmploymentStatusEnum.BUSINESS_OWNER);

        validData.setEmployment(employmentDto);

        // Мокирование возвращаемого значения для baseRate
        when(prescoringServiceMock.validate(validData)).thenReturn(true);

        // Проверка baseRate в Spring контексте
        assertEquals(10.0, scoringServiceToTest.calculateRate(validData), 0.01);
    }
    @Test
    public void testInvalidAge() {
        ScoringDataDto invalidData = new ScoringDataDto();
        invalidData.setBirthDate(LocalDate.now().minusYears(15)); // Некорректный возраст

        // Инициализация всех остальных полей, чтобы избежать NullPointerException
        invalidData.setAmount(BigDecimal.valueOf(50000)); // Пример суммы займа
        invalidData.setTerm(12); // Пример срока займа
        invalidData.setGender(GenderEnum.MALE); // Пример пола
        invalidData.setMaritalStatus(MaritalStatusEnum.SINGLE); // Пример семейного положения
        invalidData.setIsInsuranceEnabled(true); // Пример, что страховка включена
        invalidData.setIsSalaryClient(true); // Пример, что это зарплатный клиент

        // Инициализация объекта EmploymentDto
        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(BigDecimal.valueOf(30000)); // Примерная зарплата
        employment.setEmploymentStatus(EmploymentStatusEnum.SELF_EMPLOYED); // Пример статуса занятости
        employment.setWorkExperienceTotal(5); // Стаж
        employment.setWorkExperienceCurrent(2); // Стаж на текущем месте
        invalidData.setEmployment(employment);

        // Проверяем, что выбрасывается IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            scoringServiceToTest.calculateRate(invalidData);
        });
    }


    @Test
    public void testInvalidExperience() {
        // Создаем данные с некорректным стажем
        ScoringDataDto invalidData = new ScoringDataDto();
        invalidData.setBirthDate(LocalDate.now().minusYears(25)); // Корректный возраст
        invalidData.setTerm(12);
        invalidData.setAmount(BigDecimal.valueOf(50000));
        invalidData.setIsInsuranceEnabled(true);
        invalidData.setFirstName("Ivan");
        invalidData.setLastName("Ivanov");
        invalidData.setMiddleName("Ivanovich");
        invalidData.setIsSalaryClient(true);
        invalidData.setMaritalStatus(MaritalStatusEnum.MARRIED);
        invalidData.setGender(GenderEnum.MALE);

        // Создаем EmploymentDto с некорректным стажем
        EmploymentDto employmentDto = new EmploymentDto();
        employmentDto.setWorkExperienceTotal(1); // Некорректный общий стаж (меньше порогового значения)
        employmentDto.setWorkExperienceCurrent(0); // Некорректный текущий стаж
        employmentDto.setSalary(BigDecimal.valueOf(30000));
        employmentDto.setEmployerINN("123456789");
        employmentDto.setPosition(PositionEnum.MIDDLE_MANAGER);
        employmentDto.setEmploymentStatus(EmploymentStatusEnum.SELF_EMPLOYED);

        invalidData.setEmployment(employmentDto);

        // Проверяем, что выбрасывается исключение IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scoringServiceToTest.calculateRate(invalidData);
        });

        // Убеждаемся, что текст исключения правильный
        assertEquals("Отказ по стажу", exception.getMessage());
    }

    @Test
    public void testCalculateRate_invalidLoanAmount() {
        ScoringDataDto scoringData = new ScoringDataDto();
        scoringData.setAmount(BigDecimal.valueOf(250000)); // Неподходящая сумма займа

        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(BigDecimal.valueOf(10000)); // Зарплата
        scoringData.setEmployment(employment);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scoringServiceToTest.calculateRate(scoringData);
        });

        assertEquals("Отказ по сумме займа", exception.getMessage());
    }

    //FIXME
//    @Test
//    public void testCalculateRate_successfulCalculation() {
//        // Создаём данные для теста
//        ScoringDataDto scoringData = new ScoringDataDto();
//        scoringData.setAmount(BigDecimal.valueOf(500000)); // Корректная сумма займа
//        scoringData.setTerm(12); // Срок в месяцах
//        scoringData.setBirthDate(LocalDate.of(1990, 1, 1)); // Возраст > 21 года
//        scoringData.setGender(GenderEnum.MALE);
//        scoringData.setMaritalStatus(MaritalStatusEnum.MARRIED);
//        scoringData.setIsInsuranceEnabled(true);
//        scoringData.setIsSalaryClient(true);
//
//
//        EmploymentDto employment = new EmploymentDto();
//        employment.setSalary(BigDecimal.valueOf(30000)); // Зарплата
//        employment.setEmploymentStatus(EmploymentStatusEnum.BUSINESS_OWNER);
//        employment.setWorkExperienceTotal(20); // Общий стаж
//        employment.setWorkExperienceCurrent(7); // Стаж на текущем месте
//        employment.setPosition(PositionEnum.MIDDLE_MANAGER);
//        employment.setEmployerINN("123456789");
//        scoringData.setEmployment(employment);
//
//
//        // Мокируем, что prescoring прошёл
//        when(prescoringServiceMock.validate(scoringData)).thenReturn(true);
//
//        // Рассчитываем ставку
//        double rate = scoringServiceToTest.calculateRate(scoringData);
//
//        // Проверяем, что метод validate был вызван
//        verify(prescoringServiceMock, times(1)).validate(scoringData);
//
//
//        // Проверяем ожидаемый результат
//        assertEquals(3.0, rate, 0.01);  // Ожидаем ставку 3.0
//    }
    }
