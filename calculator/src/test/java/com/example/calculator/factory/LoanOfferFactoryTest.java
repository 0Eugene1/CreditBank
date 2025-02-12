package com.example.calculator.factory;

import com.example.calculator.dto.LoanStatementRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "loan.base-rate=10.0")
public class LoanOfferFactoryTest {


    @Autowired
    private LoanOfferFactory loanOfferFactory;

    private LoanStatementRequestDto validRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация стандартного валидного запроса
        validRequest = LoanStatementRequestDto.builder()
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

    }
}

