package com.example.astonrest.config.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String name;
    private int age;
    private double weight;
    private double height;
    private List<Workout> workouts;  // Список тренировок
    private List<Meal> meals;        // Список еды, которую ел пользователь
}
