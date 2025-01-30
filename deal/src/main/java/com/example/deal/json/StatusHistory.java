package com.example.deal.json;

import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class StatusHistory {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    private ChangeType changeType;

}

