package com.example.astonrest.exception;

/**
 * Исключение, выбрасываемое в случае некорректного запроса.
 */
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
