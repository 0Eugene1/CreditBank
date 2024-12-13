package com.example.deal.entity;

import com.example.deal.dto.PaymentScheduleElementDto;
import com.example.deal.enums.CreditStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
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

    @NotNull(message = "Срок кредита не может быть нулевым")
    @JsonProperty("term")
    private Integer term;

    @NotNull(message = "Ежемесячный платеж не может быть нулевым")
    private BigDecimal monthlyPayment;

    @NotNull(message = "Ставка не может быть нулевой")
    private BigDecimal rate;

    @NotNull(message = "PSK не может быть нулевым")
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

