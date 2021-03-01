package com.example.kafkabestpractices.exceptions.types;

public class UnknownActivityTypeException extends Exception {
    public UnknownActivityTypeException(String message) {
        super(message);
    }
}
