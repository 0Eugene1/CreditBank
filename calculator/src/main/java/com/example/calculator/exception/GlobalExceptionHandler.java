package com.example.calculator.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ResponseEntity<CalculatorError> handleIllegalArgumentException(IllegalArgumentException e) {
        logMessage("Invalid input detected: " + e.getMessage(), e);

        CalculatorError errorResponse = new CalculatorError("Некорректные входные данные: " + e.getMessage(),
                HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    public ResponseEntity<CalculatorError> handleNullPointerException(NullPointerException e) {
        logMessage("Null pointer exception: " + e.getMessage(), e);

        CalculatorError errorResponse = new CalculatorError("Произошла внутренняя ошибка: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CalculatorError> handleGeneralException(Exception e) {
        logMessage("Unexpected exception: " + e.getMessage(), e);

        CalculatorError errorResponse = new CalculatorError("Произошла непредвиденная ошибка. Пожалуйста, повторите попытку позже: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CalculatorError> handleValidationException(MethodArgumentNotValidException e) {
        logMessage("Validation failed: " + e.getMessage(), e);


        CalculatorError errorResponse = new CalculatorError("Invalid input data: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ResponseEntity<CalculatorError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logMessage("Invalid or missing request body: " + e.getMessage(), e);

        CalculatorError errorResponse = new CalculatorError("Некорректное или отсутствующее тело запроса",
                HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    private void logMessage(String message, Exception e) {
        log.error(message, e);
    }

}

