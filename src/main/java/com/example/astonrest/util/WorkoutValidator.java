package com.example.astonrest.util;

import com.example.astonrest.dto.WorkoutDTO;
import com.example.astonrest.exception.BadRequestException;

import java.util.regex.Pattern;

public class WorkoutValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zА-Яа-яЁё\\s-]+$");

    /**
     * Валидирует поля тренировки перед сохранением в базе данных.
     *
     * @param workoutDTO Объект DTO для тренировки, который нужно валидировать.
     * @throws BadRequestException Если одно из полей не прошло валидацию.
     */
    public static void validate(WorkoutDTO workoutDTO) {
        if (workoutDTO.getType() == null || workoutDTO.getType().trim().isEmpty()) {
            throw new BadRequestException("Workout type cannot be empty.");
        }

        if (!NAME_PATTERN.matcher(workoutDTO.getType()).matches()) {
            throw new BadRequestException("Workout type must contain only letters and spaces.");
        }

        if (workoutDTO.getDuration() <= 0) {
            throw new BadRequestException("Duration must be a positive number.");
        }
    }
}
