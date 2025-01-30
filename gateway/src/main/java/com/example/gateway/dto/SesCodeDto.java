package com.example.gateway.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SesCodeDto {
    @NotBlank(message = "SES код не может быть пустым")
    private String sesCode;
}
