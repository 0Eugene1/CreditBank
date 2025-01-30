package com.example.deal.dto;

import com.example.deal.enums.ApplicationStatus;
import com.example.deal.enums.ChangeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Статус заявки не должен быть пустым")
    @Schema(description = "Статус заявки", example = "APPROVED")
    private ApplicationStatus status;

    @NotNull(message = "Время изменения статуса не должно быть пустым")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Время изменения статуса", example = "2024-12-13T10:15:30")
    private LocalDateTime time;

    @NotNull(message = "Тип изменения статуса не должен быть пустым")
    @Schema(description = "Тип изменения статуса", example = "MANUAL")
    private ChangeType changeType;
}
