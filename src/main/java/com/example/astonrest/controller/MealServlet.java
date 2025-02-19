package com.example.astonrest.controller;

import com.example.astonrest.dto.MealDTO;
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


public class MealServlet extends HttpServlet {
    private MealService mealService;
    private final Gson gson = new Gson();
    private static final String CONTENT_TYPE = "application/json";

    @Override
    public void init() {
        this.mealService = new MealService(new MealRepository());
    }

    /**
     * Получает список всех приёмов пищи или один приём пищи по `id`
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<MealDTO> meals = mealService.getAllMeals();
                out.print(gson.toJson(meals));
            } else if (pathInfo.startsWith("/user/")) {
                int userId = Integer.parseInt(pathInfo.substring(6));
                List<MealDTO> meals = mealService.getMealsByUserId(userId);
                out.print(gson.toJson(meals));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                MealDTO meal = mealService.getMeal(id);
                if (meal == null) {
                    throw new NotFoundException("Meal not found");
                }
                out.print(gson.toJson(meal));
            }
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid meal ID format"),
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
     * Создаёт новый приём пищи (POST /meals)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        try {
            BufferedReader reader = request.getReader();
            MealDTO mealDTO = gson.fromJson(reader, MealDTO.class);

            mealService.createMeal(mealDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"message\": \"Meal created successfully\"}");

        } catch (BadRequestException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException("Internal Server Error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.flush();
    }

    /**
     * Обновляет приём пищи по ID (PUT /meals/{id})
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            ExceptionHandler.handleException(response, new BadRequestException("Meal ID is required"),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BufferedReader reader = request.getReader();
            MealDTO mealDTO = gson.fromJson(reader, MealDTO.class);

            mealService.updateMeal(id, mealDTO);
            out.print("{\"message\": \"Meal updated successfully\"}");
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid meal ID format"),
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
     * Удаляет приём пищи по ID (DELETE /meals/{id})
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            ExceptionHandler.handleException(response, new BadRequestException("Meal ID is required."),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            mealService.deleteMeal(id);
            out.print("{\"message\": \"Meal deleted successfully\"}");
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid meal ID format"),
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
