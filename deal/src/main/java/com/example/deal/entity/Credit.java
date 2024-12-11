package com.example.deal.entity;

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
import java.util.UUID;

@Entity
@Setter
@Getter
public class Credit {
    @Id
    @GeneratedValue
    private UUID creditId;

    private BigDecimal amount;
    @NotNull
    @JsonProperty("term")
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String paymentSchedule;

    private boolean insuranceEnabled;
    private boolean salaryClient;

    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;

}

