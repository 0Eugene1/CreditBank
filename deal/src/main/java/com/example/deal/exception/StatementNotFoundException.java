package com.example.deal.exception;

public class StatementNotFoundException extends RuntimeException {

    public StatementNotFoundException(String message) {
        super(message);
    }
}
