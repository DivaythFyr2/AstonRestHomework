package com.example.astonrest.controller;

import com.example.astonrest.dto.WorkoutDTO;
import com.example.astonrest.repository.WorkoutRepository;
import com.example.astonrest.service.WorkoutService;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class WorkoutServlet extends HttpServlet {
    private WorkoutService workoutService;
    private final Gson gson = new Gson();
    private static final String CONTENT_TYPE = "application/json";

    @Override
    public void init() {
        this.workoutService = new WorkoutService(new WorkoutRepository());
    }

    /**
     * Получает список всех тренировок или одну тренировку по `id`
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<WorkoutDTO> workouts = workoutService.getAllWorkouts();
            out.print(gson.toJson(workouts));
        } else if (pathInfo.equals("/user")) {

            try {
                int userId = Integer.parseInt(pathInfo.substring(6));
                List<WorkoutDTO> workouts = workoutService.getWorkoutsByUserId(userId);
                out.print(gson.toJson(workouts));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid user ID format\"}");
            }
        } else {

            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                WorkoutDTO workout = workoutService.getWorkoutById(id);
                if (workout != null) {
                    out.print(gson.toJson(workout));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Workout not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid workout ID format\"}");
            }
        }
        out.flush();
    }

    /**
     * Создаёт новую тренировку (POST /workouts)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        BufferedReader reader = request.getReader();
        WorkoutDTO workoutDTO = gson.fromJson(reader, WorkoutDTO.class);

        workoutService.createWorkout(workoutDTO, workoutDTO.getUserId());

        response.setStatus(HttpServletResponse.SC_CREATED);
        out.print("{\"message\": \"Workout created successfully\"}");
        out.flush();
    }

    /**
     * Обновляет тренировку по ID (PUT /workouts/{id})
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid ID is required\"}");
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BufferedReader reader = request.getReader();
            WorkoutDTO workoutDTO = gson.fromJson(reader, WorkoutDTO.class);

            workoutService.updateWorkout(id, workoutDTO);

            out.print("{\"message\": \"Workout updated successfully\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid workout ID format\"}");
        }
        out.flush();
    }

    /**
     * Удаляет тренировку по ID (DELETE /workouts/{id})
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Workout ID is required\"}");
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            workoutService.deleteWorkout(id);
            out.print("{\"message\": \"Workout deleted successfully\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid workout ID format\"}");
        }
        out.flush();
    }
}
