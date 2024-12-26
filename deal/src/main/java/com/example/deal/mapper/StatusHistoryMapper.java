package com.example.deal.mapper;

import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import com.example.deal.json.StatusHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StatusHistoryMapper {

    public StatusHistory toEntity(ApplicationStatus status, ChangeType changeType) {
        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setStatus(status);
        statusHistory.setTime(LocalDateTime.now());
        statusHistory.setChangeType(changeType);
        return statusHistory;
    }
}
