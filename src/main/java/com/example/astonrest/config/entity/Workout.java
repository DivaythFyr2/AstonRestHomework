package com.example.astonrest.config.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Workout {
    private int id;
    private int userId;
    private String type;
    private int duration;       // Время тренировки (минуты)
    private int caloriesBurned;
}
