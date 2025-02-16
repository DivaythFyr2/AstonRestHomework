package com.example.astonrest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO-класс для передачи информации о приёме пищи.
 * <p>
 * Мы НЕ передаём id и список пользователей, так как
 * этот объект используется только для передачи данных о пище.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealDTO {
    private String name;
    private int calories;

}
