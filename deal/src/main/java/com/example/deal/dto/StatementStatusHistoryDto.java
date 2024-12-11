package com.example.deal.dto;

import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StatementStatusHistoryDto {

    private ApplicationStatus status;
    private LocalDateTime time;
    private ChangeType changeType;
}
