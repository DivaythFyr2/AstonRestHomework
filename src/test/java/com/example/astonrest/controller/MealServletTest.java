package com.example.astonrest.controller;

import com.example.astonrest.dto.MealDTO;
import com.example.astonrest.service.MealService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServletTest {
    @Mock private MealService mealService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    private MealServlet mealServlet;
    private final Gson gson = new Gson();

    private static final List<MealDTO> EXPECTED_MEALS_DTOS = List.of(
            new MealDTO("Pasta", 500),
            new MealDTO("Salad", 200)
    );

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Создаем объект сервлета
        mealServlet = new MealServlet();

        // Устанавливаем мок в приватные поля через Рефлексию
        setField(mealServlet, "mealService", mealService);

        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
    }

    @Test
    void testGetMealById() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        when(mealService.getMeal(1)).thenReturn(EXPECTED_MEALS_DTOS.get(0));

        mealServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(mealService, times(1)).getMeal(1);
    }

    @Test
    void testGetAllMeals() throws Exception {
        when(request.getPathInfo()).thenReturn(null);
        when(mealService.getAllMeals()).thenReturn(EXPECTED_MEALS_DTOS);

        mealServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(mealService, times(1)).getAllMeals();
    }

    @Test
    void testGetMealsByUserId() throws Exception {
        int userId = 1;
        when(request.getPathInfo()).thenReturn("/user/1");
        when(mealService.getMealsByUserId(userId)).thenReturn(EXPECTED_MEALS_DTOS);

        mealServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(mealService, times(1)).getMealsByUserId(userId);
    }

    @Test
    void testCreateMeal() throws Exception {
        MealDTO newMeal = EXPECTED_MEALS_DTOS.get(0);
        String jsonRequest = gson.toJson(newMeal);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        mealServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(mealService, times(1)).createMeal(any(MealDTO.class));
    }

    @Test
    void testUpdateMeal() throws Exception {
        MealDTO updatedMeal = EXPECTED_MEALS_DTOS.get(1);
        String jsonRequest = gson.toJson(updatedMeal);

        when(request.getPathInfo()).thenReturn("/2");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        mealServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(mealService, times(1)).updateMeal(eq(2), any(MealDTO.class));
    }

    @Test
    void testDeleteMeal() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        mealServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(mealService, times(1)).deleteMeal(1);
    }

    // Метод для установки приватного поля через Рефлексию
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}