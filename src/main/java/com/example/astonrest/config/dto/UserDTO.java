package com.example.astonrest.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO-класс для передачи информации о пользователе.
 * <p>
 * Мы НЕ передаём id и списки тренировок и еды,
 * так как это внутренние детали, которые клиенту не нужны.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private int age;
    private double weight;
    private double height;

}
