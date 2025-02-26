package com.example.astonrest.controller;

import com.example.astonrest.constants.ApiConstants;
import com.example.astonrest.dto.MealDTO;
import com.example.astonrest.dto.MessageResponseDTO;
import com.example.astonrest.exception.BadRequestException;
import com.example.astonrest.exception.CustomException;
import com.example.astonrest.exception.ExceptionHandler;
import com.example.astonrest.exception.NotFoundException;
import com.example.astonrest.repository.MealRepository;
import com.example.astonrest.service.MealService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Сервлет для управления приёмами пищи.
 * Обрабатывает HTTP-запросы для получения, создания, обновления и удаления приёмов пищи.
 */
public class MealServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private MealService mealService;

    @Override
    public void init() {
        this.mealService = new MealService(new MealRepository());
    }

    /**
     * Получает список всех приёмов пищи или один приём пищи по `id`
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        try {
            Object responseBody = isRootPath(pathInfo) ? getAllMeals() : processMealRequest(pathInfo);

            out.print(gson.toJson(responseBody));
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_MEAL_ID),
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
     * Создаёт новый приём пищи (POST /meals)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        try {
            BufferedReader reader = request.getReader();
            MealDTO mealDTO = gson.fromJson(reader, MealDTO.class);

            mealService.createMeal(mealDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(new MessageResponseDTO(ApiConstants.MEAL_CREATED_SUCCESSFULLY).toJson());

        } catch (BadRequestException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException(ApiConstants.INTERNAL_SERVER_ERROR),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.flush();
    }

    /**
     * Обновляет приём пищи по ID (PUT /meals/{id})
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (isRootPath(pathInfo)) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.MEAL_ID_IS_REQUIRED),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BufferedReader reader = request.getReader();
            MealDTO mealDTO = gson.fromJson(reader, MealDTO.class);

            mealService.updateMeal(id, mealDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(new MessageResponseDTO(ApiConstants.MEAL_UPDATED_SUCCESSFULLY).toJson());
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_MEAL_ID),
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
     * Удаляет приём пищи по ID (DELETE /meals/{id})
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupResponse(response);

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (isRootPath(pathInfo)) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.MEAL_ID_IS_REQUIRED),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            mealService.deleteMeal(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            out.print(new MessageResponseDTO(ApiConstants.MEAL_DELETED_SUCCESSFULLY).toJson());
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException(ApiConstants.INVALID_MEAL_ID),
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
     * Обрабатывает запрос на получение конкретного приёма пищи или списка приёмов пользователя.
     */
    private Object processMealRequest(String pathInfo) {
        if(pathInfo.startsWith(ApiConstants.USER_PATH_WITH_SLASH)) {
            return getMealsByUserId(pathInfo.substring(ApiConstants.USER_PATH_WITH_SLASH.length()));
        } else {
            return getMealById(pathInfo.substring(1));
        }
    }

    /**
     * Получает список всех приёмов пищи.
     */
    private List<MealDTO> getAllMeals() {
        return mealService.getAllMeals();
    }

    /**
     * Получает приёмы пищи пользователя по id.
     */
    private List<MealDTO> getMealsByUserId(String userIdStr) {
        int userId = Integer.parseInt(userIdStr);
        return mealService.getMealsByUserId(userId);
    }

    /**
     * Получает приём пищи по id.
     */
    private MealDTO getMealById(String mealIdStr) {
        int mealId = Integer.parseInt(mealIdStr);
        MealDTO meal = mealService.getMeal(mealId);
        if(meal == null) {
            throw new NotFoundException(ApiConstants.MEAL_NOT_FOUND);
        }
        return meal;
    }

    /**
     * Устанавливает заголовки ответа.
     */
    private void setupResponse(HttpServletResponse response) {
        response.setContentType(ApiConstants.CONTENT_TYPE);
        response.setCharacterEncoding(ApiConstants.CHARACTER_ENCODING);
    }
}
