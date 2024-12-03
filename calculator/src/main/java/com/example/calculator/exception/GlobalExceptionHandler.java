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
        logMessage("error", "Invalid input detected: " + e.getMessage(), e);

        CalculatorError errorResponse = new CalculatorError("Некорректные входные данные." ,
                HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    public ResponseEntity<CalculatorError> handleNullPointerException(NullPointerException e) {
        logMessage("error", "Null pointer exception: " + e.getMessage(), e);

        CalculatorError errorResponse = new CalculatorError("Произошла внутренняя ошибка",
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<CalculatorError> handleGeneralException(Exception e) {
        logMessage("error", "Unexpected exception: " + e.getMessage(), e);

        CalculatorError errorResponse = new CalculatorError("Произошла непредвиденная ошибка. Пожалуйста, повторите попытку позже." ,
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CalculatorError> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Invalid input data");

        CalculatorError errorResponse = new CalculatorError(errorMessage, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ResponseEntity<CalculatorError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Invalid or missing request body: {}", e.getMessage());

        CalculatorError errorResponse = new CalculatorError("Некорректное или отсутствующее тело запроса",
                HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



    private void logMessage(String logType, String message, Exception e) {
        switch (logType.toLowerCase()) {
            case "info":
                log.info(message, e);
                break;
            case "warn":
                log.warn(message, e);
                break;
            case "error":
                log.error(message, e);
                break;
            default:
                log.debug("Unsupported type: {}", logType);
        }
    }
}
