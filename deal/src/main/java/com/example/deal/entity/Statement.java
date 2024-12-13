package com.example.deal.entity;

import com.example.deal.enums.ApplicationStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID", nullable = false, updatable = false)
    private UUID statementId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false) //
    private Client client;

    @ManyToOne
    @JoinColumn(name = "credit_id", nullable = false) //
    private Credit credit;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime creationDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @NotNull(message = "Applied Offer не может быть пустым")
    private String appliedOffer;

    private LocalDateTime signDate;
    private String sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @NotNull(message = "История статусов не может быть пустой")
    private String statusHistory;

}