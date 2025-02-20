package com.example.astonrest.controller;

import com.example.astonrest.dto.WorkoutDTO;
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
class WorkoutServletTest {
    @Mock
    private WorkoutService workoutService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private WorkoutServlet workoutServlet;
    private final Gson gson = new Gson();

    private static final List<WorkoutDTO> EXPECTED_WORKOUTS_DTOS = List.of(
            new WorkoutDTO("Running", 30, 360, 1),
            new WorkoutDTO("Cycling", 45, 315, 2)
    );

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Создаем объект сервлета
        workoutServlet = new WorkoutServlet();

        // Устанавливаем мок в приватные поля через Рефлексию
        setField(workoutServlet, "workoutService", workoutService);

        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
    }

    @Test
    void testGetWorkoutById() throws Exception {
        when(workoutService.getWorkoutById(1)).thenReturn(EXPECTED_WORKOUTS_DTOS.get(0));
        when(request.getPathInfo()).thenReturn("/1");

        workoutServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(workoutService, times(1)).getWorkoutById(1);
    }

    @Test
    void testGetAllWorkouts() throws Exception {
        when(workoutService.getAllWorkouts()).thenReturn(EXPECTED_WORKOUTS_DTOS);
        when(request.getPathInfo()).thenReturn("/");

        workoutServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(workoutService, times(1)).getAllWorkouts();
    }

    @Test
    void testGetUserWorkouts() throws IOException {
        int userId = 1;
        List<WorkoutDTO> expectedWorkouts = EXPECTED_WORKOUTS_DTOS;

        when(request.getPathInfo()).thenReturn("/users/1");
        when(workoutService.getWorkoutsByUserId(userId)).thenReturn(expectedWorkouts);

        workoutServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(workoutService, times(1)).getWorkoutsByUserId(userId);
    }

    @Test
    void testCreateWorkoutForUser() throws Exception {
        int userId = 1;
        WorkoutDTO expectedWorkout = EXPECTED_WORKOUTS_DTOS.get(0);
        String jsonRequest = gson.toJson(expectedWorkout);

        when(request.getPathInfo()).thenReturn("/users/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        workoutServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(workoutService, times(1)).createWorkoutForUser(any(WorkoutDTO.class), eq(1));
    }

    @Test
    void testUpdateWorkout() throws Exception {
        WorkoutDTO updatedWorkout = EXPECTED_WORKOUTS_DTOS.get(1);
        String jsonRequest = gson.toJson(updatedWorkout);

        when(request.getPathInfo()).thenReturn("/2");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        workoutServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(workoutService, times(1)).updateWorkout(eq(2), any(WorkoutDTO.class));
    }

    @Test
    void testDeleteWorkout() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        workoutServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(workoutService, times(1)).deleteWorkout(1);
    }

    // Метод для установки приватного поля через Рефлексию
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}