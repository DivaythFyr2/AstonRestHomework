package com.example.astonrest.controller;

import com.example.astonrest.constants.ApiConstants;
import com.example.astonrest.dto.MessageResponseDTO;
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

/**
 * Сервлет для управления тренировками.
 * Обрабатывает HTTP-запросы для получения, создания, обновления и удаления тренировок.
 * Также поддерживает получение тренировок для конкретного пользователя.
 */
public class WorkoutServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private WorkoutService workoutService;

    @Override
    public void init() {
        this.workoutService = new WorkoutService(new WorkoutRepository(), new UserRepository());
    }

    /**
     * Получает список всех тренировок или одну тренировку по `id`
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        try {
            Object responseBody = isRootPath(pathInfo) ? getAllWorkouts() : processWorkoutRequest(pathInfo);

            out.print(gson.toJson(responseBody));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_WORKOUT_ID),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (NotFoundException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException(ApiConstants.INTERNAL_SERVER_ERROR),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        out.flush();
    }

    /**
     * Создаёт новую тренировку (POST /workouts)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (!isValidWorkoutPath(pathInfo)) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_WORKOUT_REQUEST_FORMAT),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int userId = extractUserId(pathInfo);
            BufferedReader reader = request.getReader();
            WorkoutDTO workoutDTO = gson.fromJson(reader, WorkoutDTO.class);

            workoutService.createWorkoutForUser(workoutDTO, userId);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(new MessageResponseDTO(ApiConstants.WORKOUT_CREATED_SUCCESSFULLY).toJson());

        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_USER_ID),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (BadRequestException | NotFoundException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException(ApiConstants.INTERNAL_SERVER_ERROR),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.flush();
    }

    /**
     * Обновляет тренировку по ID (PUT /workouts/{id})
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (isRootPath(pathInfo)) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.WORKOUT_ID_IS_REQUIRED),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BufferedReader reader = request.getReader();
            WorkoutDTO workoutDTO = gson.fromJson(reader, WorkoutDTO.class);

            workoutService.updateWorkout(id, workoutDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(new MessageResponseDTO(ApiConstants.WORKOUT_UPDATED_SUCCESSFULLY).toJson());
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_WORKOUT_ID),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (NotFoundException | BadRequestException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException(ApiConstants.INTERNAL_SERVER_ERROR),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.flush();
    }

    /**
     * Удаляет тренировку по ID (DELETE /workouts/{id})
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (isRootPath(pathInfo)) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.WORKOUT_ID_IS_REQUIRED),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            workoutService.deleteWorkout(id);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(new MessageResponseDTO(ApiConstants.WORKOUT_DELETED_SUCCESSFULLY).toJson());

        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_WORKOUT_ID),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (NotFoundException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException(ApiConstants.INTERNAL_SERVER_ERROR),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.flush();
    }

    // Вспомогательные методы для разгрузки сервлетов (Они не должны находиться в классе Servlet)

    /**
     * Проверяет, является ли путь корневым.
     */
    private boolean isRootPath(String pathInfo) {
        return pathInfo == null || pathInfo.equals(ApiConstants.ROOT_PATH);
    }

    /**
     * Проверяет, соответствует ли путь допустимым эндпоинтам для создания тренировки.
     */
    private boolean isValidWorkoutPath(String pathInfo) {
        return pathInfo != null && (pathInfo.startsWith(ApiConstants.USERS_PATH_WITH_SLASH) ||
                pathInfo.startsWith(ApiConstants.WORKOUTS_USERS_PATH_WITH_SLASH));
    }

    /**
     * Обрабатывает запрос на получение конкретной тренировки или списка тренировок пользователя.
     */
    private Object processWorkoutRequest(String pathInfo) {
        String[] pathParts = pathInfo.split("/");

        if(pathParts.length == 2) {
            return getWorkoutById(pathParts[1]);
        } else if (pathParts.length == 3 && ApiConstants.USERS_PATH.equals(pathParts[1])) {
            return getWorkoutsByUserId(pathParts[2]);
        } else {
            throw new BadRequestException(ApiConstants.INVALID_REQUEST);
        }
    }

    /**
     * Получает список всех тренировок.
     */
    private List<WorkoutDTO> getAllWorkouts() {
        return workoutService.getAllWorkouts();
    }

    /**
     * Получает тренировки пользователя по id.
     */
    private List<WorkoutDTO> getWorkoutsByUserId(String userIdStr) {
        int userId = Integer.parseInt(userIdStr);
        return workoutService.getWorkoutsByUserId(userId);
    }

    /**
     * Извлекает ID пользователя из пути.
     */
    private int extractUserId(String pathInfo) {
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length == 3 && ApiConstants.USERS_PATH.equals(pathParts[1])) {
            return Integer.parseInt(pathParts[2]);
        }
        throw new BadRequestException(ApiConstants.INVALID_WORKOUT_REQUEST_FORMAT);
    }

    /**
     * Получает тренировку по id.
     */
    private WorkoutDTO getWorkoutById(String workoutIdStr) {
        int workoutId = Integer.parseInt(workoutIdStr);
        WorkoutDTO workout = workoutService.getWorkoutById(workoutId);
        if(workout == null) {
            throw new NotFoundException(ApiConstants.WORKOUT_NOT_FOUND);
        }
        return workout;
    }

    /**
     * Устанавливает заголовки ответа.
     */
    private void setupResponse(HttpServletResponse response) {
        response.setContentType(ApiConstants.CONTENT_TYPE);
        response.setCharacterEncoding(ApiConstants.CHARACTER_ENCODING);
    }
}
