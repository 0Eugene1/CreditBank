package com.example.deal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CalculatorError {
    private String message;
    private int statusCode;
}
