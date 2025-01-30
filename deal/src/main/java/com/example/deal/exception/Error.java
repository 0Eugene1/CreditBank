package com.example.deal.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Standard error response")
public class Error {
    @Schema(description = "Error code", example = "400")
    private String code;

    @Schema(description = "Error message", example = "Invalid request data")
    private String message;

}
