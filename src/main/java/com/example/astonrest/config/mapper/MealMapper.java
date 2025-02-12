package com.example.astonrest.config.mapper;

import com.example.astonrest.config.dto.MealDTO;
import com.example.astonrest.config.entity.Meal;

public class MealMapper {

    /**
     * Преобразует сущность Meal в DTO.
     *
     * @param meal объект Meal
     * @return объект MealDTO без ID и списка пользователей
     */
    public static MealDTO toDTO(Meal meal) {
        return new MealDTO(meal.getName(), meal.getCalories());
    }

    /**
     * Преобразует DTO в сущность Meal.
     * ID устанавливается на 0, так как он задаётся в базе.
     *
     * @param mealDTO объект MealDTO
     * @return объект Meal без списка пользователей
     */
    public static Meal toEntity(MealDTO mealDTO) {
        return new Meal(0,mealDTO.getName(),mealDTO.getCalories(), null);
    }
}
