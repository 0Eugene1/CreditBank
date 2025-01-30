package com.example.deal.dto;

import com.example.deal.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StatementDto {
    private UUID statementId;
    private ApplicationStatus status;
    private LocalDateTime creationDate;
    private LoanOfferDto appliedOffer;
    private LocalDateTime signDate;
    private String sesCode;
}
