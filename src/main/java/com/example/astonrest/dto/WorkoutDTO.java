package com.example.astonrest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO-класс для передачи информации о тренировке.
 * <p>
 * Мы НЕ передаём id
 * так как эти детали обрабатываются на сервере.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutDTO {
    private String type;
    private int duration;
    private int caloriesBurned;
    private int userId;
}
