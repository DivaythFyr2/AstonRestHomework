package com.example.astonrest.controller;

import com.example.astonrest.constants.ApiConstants;
import com.example.astonrest.dto.MessageResponseDTO;
import com.example.astonrest.dto.UserDTO;
import com.example.astonrest.exception.BadRequestException;
import com.example.astonrest.exception.CustomException;
import com.example.astonrest.exception.ExceptionHandler;
import com.example.astonrest.exception.NotFoundException;
import com.example.astonrest.repository.MealRepository;
import com.example.astonrest.repository.UserRepository;
import com.example.astonrest.repository.WorkoutRepository;
import com.example.astonrest.service.MealService;
import com.example.astonrest.service.UserService;
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
 * Сервлет для управления пользователями.
 * Обрабатывает HTTP-запросы для получения, создания, обновления и удаления пользователей.
 * Также поддерживает получение тренировок и приёмов пищи пользователя.
 */
public class UserServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private UserService userService;
    private WorkoutService workoutService;
    private MealService mealService;

    @Override
    public void init() {
        this.userService = new UserService(new UserRepository());
        this.workoutService = new WorkoutService(new WorkoutRepository(), new UserRepository());
        this.mealService = new MealService(new MealRepository());
    }

    /**
     * Получает список всех пользователей или одного пользователя по `id`
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        try {
            Object responseBody;

            if (isRootPath(pathInfo)) {
                responseBody = getAllUsers();
            } else {
                responseBody = processUserRequest(pathInfo);
            }
            out.print(gson.toJson(responseBody));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_USER_ID),
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
     * Создаёт нового пользователя (POST /users)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        try {
            BufferedReader reader = request.getReader();
            UserDTO userDTO = gson.fromJson(reader, UserDTO.class);

            userService.createUser(userDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(new MessageResponseDTO(ApiConstants.USER_CREATED_SUCCESSFULLY).toJson());
        } catch (BadRequestException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException(ApiConstants.INTERNAL_SERVER_ERROR),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.flush();
    }

    /**
     * Обновляет пользователя по ID (PUT /users/{id})
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (isRootPath(pathInfo)) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.USER_ID_IS_REQUIRED),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BufferedReader reader = request.getReader();
            UserDTO userDTO = gson.fromJson(reader, UserDTO.class);
            userService.updateUser(id, userDTO);

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(new MessageResponseDTO(ApiConstants.USER_UPDATED_SUCCESSFULLY).toJson());
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_USER_ID),
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
     * Удаляет пользователя по ID (DELETE /users/{id})
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (isRootPath(pathInfo)) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.USER_ID_IS_REQUIRED),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            userService.deleteUser(id);

            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            out.print(new MessageResponseDTO(ApiConstants.USER_DELETED_SUCCESSFULLY).toJson());
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_USER_ID),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (NotFoundException | BadRequestException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
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
     * Обрабатывает запрос пользователя по id, а также его тренировок и приемов пищи.
     */
    private Object processUserRequest(String pathInfo) {
        String[] pathParts = pathInfo.split("/");

        if (pathParts.length == 2) {
            return getUserById(pathParts[1]);
        } else if (pathParts.length == 3) {
            return getUserRelatedData(pathParts[1], pathParts[2]);
        } else {
            throw new BadRequestException(ApiConstants.INVALID_REQUEST);
        }
    }

    /**
     * Получает список всех пользователей.
     */
    private List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Получает пользователя по ID.
     */
    private UserDTO getUserById(String userIdStr) {
        int userId = Integer.parseInt(userIdStr);
        UserDTO user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException(ApiConstants.USER_NOT_FOUND);
        }
        return user;
    }

    private Object getUserRelatedData(String userIdStr, String type) {
        int userId = Integer.parseInt(userIdStr);
        if (ApiConstants.WORKOUTS_PATH.equals(type)) {
            return workoutService.getWorkoutsByUserId(userId);
        } else if (ApiConstants.MEALS_PATH.equals(type)) {
            return mealService.getMealsByUserId(userId);
        } else {
            throw new BadRequestException(ApiConstants.INVALID_REQUEST);
        }
    }

    /**
     * Устанавливает заголовки ответа.
     */
    private void setupResponse(HttpServletResponse response) {
        response.setContentType(ApiConstants.CONTENT_TYPE);
        response.setCharacterEncoding(ApiConstants.CHARACTER_ENCODING);
    }
}
