package com.example.deal.entity;

import com.example.deal.dto.PaymentScheduleElementDto;
import com.example.deal.enums.CreditStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Credit {

    @Id
    @GeneratedValue
    private UUID creditId;

    @NotNull(message = "Сумма кредита не может быть нулевой")
    private BigDecimal amount;

    private Integer term;

    private BigDecimal monthlyPayment;

    @NotNull(message = "Ставка не может быть нулевой")
    private BigDecimal rate;

    private BigDecimal psk;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<PaymentScheduleElementDto> paymentSchedule;

    private boolean insuranceEnabled;
    private boolean salaryClient;

    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;

    @OneToMany(mappedBy = "credit")
    private List<Statement> statements; // Обратная связь

}

