package com.example.astonrest.service;

import com.example.astonrest.dto.MealDTO;
import com.example.astonrest.entity.Meal;
import com.example.astonrest.exception.NotFoundException;
import com.example.astonrest.mapper.MealMapper;
import com.example.astonrest.repository.MealRepository;
import com.example.astonrest.util.MealValidator;

import java.util.List;
import java.util.stream.Collectors;

public class MealService {
    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    /**
     * Создаёт новый приём пищи.
     * @param mealDTO DTO еды
     */
    public void createMeal(MealDTO mealDTO) {
        MealValidator.validate(mealDTO);
        Meal meal = MealMapper.toEntity(mealDTO);
        mealRepository.save(meal);
    }

    /**
     * Получает приём пищи по ID.
     * @param id ID еды
     * @return DTO еды
     */
    public MealDTO getMeal(int id) {
        Meal meal = mealRepository.findMealById(id);
        return (meal != null) ? MealMapper.toDTO(meal) : null;
    }

    /**
     * Получает список всех приёмов пищи.
     * @return список еды в формате DTO
     */
    public List<MealDTO> getAllMeals() {
        return mealRepository.findAllMeals()
                .stream()
                .map(MealMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет данные приёма пищи.
     * @param id ID еды
     * @param mealDTO Обновлённые данные
     */
    public void updateMeal(int id, MealDTO mealDTO) {
        Meal existingMeal = mealRepository.findMealById(id);
        if (existingMeal != null) {
            existingMeal.setName(mealDTO.getName());
            existingMeal.setCalories(mealDTO.getCalories());
            mealRepository.update(existingMeal);
        }
    }

    /**
     * Удаляет приём пищи по ID.
     * @param id ID еды
     */
    public void deleteMeal(int id) {
        MealDTO meal = getMeal(id);
        if(meal == null) {
            throw new NotFoundException("Meal with ID " + id + " not found.");
        }
        mealRepository.delete(id);
    }

    /**
     * Получает все приёмы пищи для конкретного пользователя.
     * @param userId ID пользователя
     * @return список еды в формате DTO
     */
    public List<MealDTO> getMealsByUserId(int userId) {
        return mealRepository.findMealsByUserId(userId)
                .stream()
                .map(MealMapper::toDTO)
                .collect(Collectors.toList());
    }
}
