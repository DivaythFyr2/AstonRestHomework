package com.example.astonrest.dto;

import com.google.gson.Gson;

/**
 * DTO для передачи стандартных сообщений в JSON-ответах сервлетов.
 */
public class MessageResponseDTO {
    private String message;

    public MessageResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
