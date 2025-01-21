package com.example.deal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SesCodeDTO {
    @NotBlank(message = "SES код не может быть пустым")
    private String sesCode;
}
