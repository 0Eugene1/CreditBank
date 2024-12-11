package com.example.deal.dto;

import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatementStatusHistoryDto {

    private ApplicationStatus status;
    private LocalDateTime time;
    private ChangeType changeType;
}
