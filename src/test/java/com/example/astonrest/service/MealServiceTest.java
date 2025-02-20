package com.example.astonrest.service;

import com.example.astonrest.dto.MealDTO;
import com.example.astonrest.entity.Meal;
import com.example.astonrest.repository.MealRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private MealService mealService;

    private static List<Meal> meals;
    private static List<MealDTO> mealDTOs;

    @BeforeAll
    static void setUp() {
        meals = Arrays.asList(new Meal(1,"Pasta", 500, new ArrayList<>()),
                new Meal(2, "Salad", 200, new ArrayList<>()));

        mealDTOs = meals.stream()
                .map(meal -> new MealDTO(meal.getName(), meal.getCalories()))
                .collect(Collectors.toList());
    }

    @BeforeEach
    void init() {
        mealService = new MealService(mealRepository);
    }

    @Test
    void testCreateMeal() {
        MealDTO existingMeal = mealDTOs.get(0);
        // Создаем ожидаемый Meal объект, который будет передан в репозиторий
        Meal expectedMeal = new Meal(0,existingMeal.getName(),existingMeal.getCalories(),new ArrayList<>());

        // Мокаем метод save, чтобы он не выполнял реальные операции
        doNothing().when(mealRepository).save(any(Meal.class));

        mealService.createMeal(existingMeal);

        // Используем ArgumentCaptor для захвата аргумента, передаваемого в save
        ArgumentCaptor<Meal> mealCaptor = ArgumentCaptor.forClass(Meal.class);
        verify(mealRepository, times(1)).save(mealCaptor.capture());

        Meal capturedMeal = mealCaptor.getValue();
        // Проверяем, что имя и калории захваченного Meal совпадают с ожидаемыми значениями
        assertEquals(existingMeal.getName(),capturedMeal.getName());
        assertEquals(existingMeal.getCalories(),capturedMeal.getCalories());
    }

    @Test
    void testGetMealById() {
        Meal expectedMeal = meals.get(0);
        when(mealRepository.findMealById(1)).thenReturn(expectedMeal);

        MealDTO actualMealDTO = mealService.getMeal(1);

        assertEquals(expectedMeal.getName(),actualMealDTO.getName());
        assertEquals(expectedMeal.getCalories(),actualMealDTO.getCalories());
    }

    @Test
    void testGetAllMeals() {
        when(mealRepository.findAllMeals()).thenReturn(meals);

        List<MealDTO> actualMealDTOs = mealService.getAllMeals();

        assertNotNull(actualMealDTOs);
        assertEquals(mealDTOs.size(), actualMealDTOs.size());
        assertEquals(mealDTOs.get(0).getName(), actualMealDTOs.get(0).getName());
        assertEquals(mealDTOs.get(0).getCalories(), actualMealDTOs.get(0).getCalories());
        assertEquals(mealDTOs.get(1).getName(), actualMealDTOs.get(1).getName());
        assertEquals(mealDTOs.get(1).getCalories(), actualMealDTOs.get(1).getCalories());
    }

    @Test
    void testUpdateMeal() {
        MealDTO updatedMealDTO = mealDTOs.get(0);
        Meal existingMeal = new Meal(2, updatedMealDTO.getName(),updatedMealDTO.getCalories(),new ArrayList<>());

        when(mealRepository.findMealById(2)).thenReturn(existingMeal);
        doNothing().when(mealRepository).update(existingMeal);

        mealService.updateMeal(2, updatedMealDTO);

        verify(mealRepository, times(1)).update(existingMeal);
    }

    @Test
    void testDeleteMeal() {
        MealDTO deletedMealDTO = mealDTOs.get(0);
        Meal existingMeal = new Meal(1,deletedMealDTO.getName(),deletedMealDTO.getCalories(),new ArrayList<>());

        when(mealRepository.findMealById(1)).thenReturn(existingMeal);
        doNothing().when(mealRepository).delete(1);

        mealService.deleteMeal(1);

        verify(mealRepository, times(1)).delete(1);
    }

    @Test
    void testGetMealsByUserId() {
        int userId = 1;
        when(mealRepository.findMealsByUserId(userId)).thenReturn(meals);

        List<MealDTO> actualMealDTOs = mealService.getMealsByUserId(userId);

        assertNotNull(actualMealDTOs);
        assertEquals(mealDTOs.size(), actualMealDTOs.size());
        assertEquals(mealDTOs.get(0).getName(), actualMealDTOs.get(0).getName());
        assertEquals(mealDTOs.get(0).getCalories(), actualMealDTOs.get(0).getCalories());
    }
}