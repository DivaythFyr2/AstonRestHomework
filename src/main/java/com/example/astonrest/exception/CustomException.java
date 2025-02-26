package com.example.astonrest.exception;

/**
 * Исключение, представляющее общую ошибку сервера.
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
