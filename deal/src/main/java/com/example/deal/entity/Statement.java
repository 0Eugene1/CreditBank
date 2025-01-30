package com.example.deal.entity;

import com.example.deal.dto.LoanOfferDto;
import com.example.deal.enums.ApplicationStatus;
import com.example.deal.json.StatusHistory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    @JsonBackReference
    private Client client;

    @ManyToOne
    @JoinColumn(name = "credit_id", nullable = false)
    @JsonBackReference
    private Credit credit;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime creationDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private LoanOfferDto appliedOffer;

    private LocalDateTime signDate;
    private String sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @JsonBackReference
    private List<StatusHistory> statusHistory;

}