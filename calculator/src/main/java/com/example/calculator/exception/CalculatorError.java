package com.example.calculator.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CalculatorError {

    private String error;
    private Integer status;

    public CalculatorError(String error, Integer status) {
        this.error = error;
        this.status = status;
    }
}
