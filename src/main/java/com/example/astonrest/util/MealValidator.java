package com.example.astonrest.util;

import com.example.astonrest.dto.MealDTO;
import com.example.astonrest.exception.BadRequestException;

import java.util.regex.Pattern;

public class MealValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zА-Яа-яЁё\\s-]+$");

    /**
     * Валидирует поля приема пищи перед сохранением в базе данных.
     *
     * @param mealDTO Объект DTO для еды, который нужно валидировать.
     * @throws BadRequestException Если одно из полей не прошло валидацию.
     */
    public static void validate(MealDTO mealDTO) {
        if (mealDTO.getName() == null || mealDTO.getName().trim().isEmpty()) {
            throw new BadRequestException("Meal name cannot be empty.");
        }

        if (!NAME_PATTERN.matcher(mealDTO.getName()).matches()) {
            throw new BadRequestException("Meal name must contain only letters and spaces.");
        }

        if (mealDTO.getCalories() <= 0) {
            throw new BadRequestException("Calories must be a positive number.");
        }
    }
}

