package com.example.deal.entity;

import com.example.deal.enums.ApplicationStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor // Добавляет конструктор без параметров
@AllArgsConstructor
@Builder
public class Statement {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID", nullable = false, updatable = false)
    private UUID statementId;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime creationDate;

    @JdbcTypeCode(SqlTypes.JSON)  // Указываем, что поле имеет тип jsonb
    @Column(columnDefinition = "jsonb")
    @NotEmpty
    private String appliedOffer;

    private LocalDateTime signDate;
    private String sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @NotEmpty
    private String statusHistory;


}
