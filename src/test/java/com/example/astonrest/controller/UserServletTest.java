package com.example.astonrest.controller;

import com.example.astonrest.dto.MealDTO;
import com.example.astonrest.dto.UserDTO;
import com.example.astonrest.dto.WorkoutDTO;
import com.example.astonrest.service.MealService;
import com.example.astonrest.service.UserService;
import com.example.astonrest.service.WorkoutService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServletTest {
    @Mock
    private UserService userService;
    @Mock
    private WorkoutService workoutService;
    @Mock
    private MealService mealService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private UserServlet userServlet;
    private final Gson gson = new Gson();

    private static final List<UserDTO> EXPECTED_USERS_DTOS = List.of(
            new UserDTO("Alice", 28, 60.0, 170),
            new UserDTO("Bob", 32, 82.5, 185)
    );


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Создаем объект сервлета
        userServlet = new UserServlet();

        // Устанавливаем моки в приватные поля через Рефлексию
        setField(userServlet, "userService", userService);
        setField(userServlet, "workoutService", workoutService);
        setField(userServlet, "mealService", mealService);

        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
    }

    @Test
    void testGetUserById() throws Exception {
        when(request.getPathInfo()).thenReturn("/1"); // Симулируем путь /users/1
        when(userService.getUserById(1)).thenReturn(EXPECTED_USERS_DTOS.get(0));

        userServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK); // Проверяем, что статус 200
        verify(userService, times(1)).getUserById(1); // Проверяем вызов метода сервиса
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(request.getPathInfo()).thenReturn(null);
        when(userService.getAllUsers()).thenReturn(EXPECTED_USERS_DTOS);

        userServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserWorkouts() throws IOException {
        int userId = 1;
        List<WorkoutDTO> expectedWorkouts = List.of(
                new WorkoutDTO("Running", 30, 360, userId),
                new WorkoutDTO("Swimming", 45, 400, userId)
        );

        when(request.getPathInfo()).thenReturn("/1/workouts");
        when(workoutService.getWorkoutsByUserId(userId)).thenReturn(expectedWorkouts);

        userServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(workoutService, times(1)).getWorkoutsByUserId(userId);
    }

    @Test
    void testGetUserMeals() throws IOException {
        int userId = 1;
        List<MealDTO> expectedMeals = List.of(
                new MealDTO("Pasta", 500),
                new MealDTO("Salad", 200)
        );

        when(request.getPathInfo()).thenReturn("/1/meals");
        when(mealService.getMealsByUserId(userId)).thenReturn(expectedMeals);

        userServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(mealService, times(1)).getMealsByUserId(userId);
    }

    @Test
    void testCreateUser() throws Exception {
        UserDTO newUser = EXPECTED_USERS_DTOS.get(0);
        String jsonRequest = gson.toJson(newUser);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        userServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDTO updatedUser = EXPECTED_USERS_DTOS.get(1);
        String jsonRequest = gson.toJson(updatedUser);
        when(request.getPathInfo()).thenReturn("/2");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        userServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(userService, times(1)).updateUser(eq(2), any(UserDTO.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        userServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(userService, times(1)).deleteUser(1);
    }

    // Метод для установки приватного поля через Рефлексию
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}