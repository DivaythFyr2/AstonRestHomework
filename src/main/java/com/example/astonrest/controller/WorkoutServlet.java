package com.example.astonrest.controller;

import com.example.astonrest.dto.WorkoutDTO;
import com.example.astonrest.exception.BadRequestException;
import com.example.astonrest.exception.CustomException;
import com.example.astonrest.exception.ExceptionHandler;
import com.example.astonrest.exception.NotFoundException;
import com.example.astonrest.repository.UserRepository;
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
        this.workoutService = new WorkoutService(new WorkoutRepository(), new UserRepository());
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

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<WorkoutDTO> workouts = workoutService.getAllWorkouts();
                out.print(gson.toJson(workouts));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int workoutId = Integer.parseInt(pathParts[1]);
                    WorkoutDTO workout = workoutService.getWorkoutById(workoutId);
                    if (workout != null) {
                        out.print(gson.toJson(workout));
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        throw new NotFoundException("Workout not found");
                    }
                } else if (pathParts.length == 3 && "users".equals(pathParts[1])) {
                    int userId = Integer.parseInt(pathParts[2]);
                    List<WorkoutDTO> workouts = workoutService.getWorkoutsByUserId(userId);
                    out.print(gson.toJson(workouts));
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    throw new BadRequestException("Invalid request format");
                }
            }
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid ID format"),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (NotFoundException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException("Internal Server Error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
        String pathInfo = request.getPathInfo();
        System.out.println("DEBUG: Received POST request, pathInfo = " + pathInfo);

        if (pathInfo == null || !pathInfo.startsWith("/users/")) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid endpoint. Use /users/{id}/workouts"),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] pathParts = pathInfo.split("/");

        if (pathParts.length != 3 || !pathParts[1].equals("users")) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid request Format. Use /workouts/users/{id}"),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int userId = Integer.parseInt(pathParts[2]);
            BufferedReader reader = request.getReader();
            WorkoutDTO workoutDTO = gson.fromJson(reader, WorkoutDTO.class);
            System.out.println("DEBUG: Parsed WorkoutDTO: " + gson.toJson(workoutDTO));

            workoutService.createWorkoutForUser(workoutDTO, userId);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"message\": \"Workout created successfully\"}");

        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid user ID format"),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (BadRequestException | NotFoundException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException("Internal Server Error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

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
            ExceptionHandler.handleException(response, new BadRequestException("Invalid ID is required."),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BufferedReader reader = request.getReader();
            WorkoutDTO workoutDTO = gson.fromJson(reader, WorkoutDTO.class);

            workoutService.updateWorkout(id, workoutDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"message\": \"Workout updated successfully\"}");
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid workout ID format"),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (NotFoundException | BadRequestException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException("Internal Server Error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
            ExceptionHandler.handleException(response, new BadRequestException("Workout ID is required."),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            workoutService.deleteWorkout(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            out.print("{\"message\": \"Workout deleted successfully\"}");

        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid workout ID format"),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (NotFoundException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException("Internal Server Error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.flush();
    }
}
