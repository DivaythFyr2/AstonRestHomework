package com.example.astonrest.controller;

import com.example.astonrest.dto.MealDTO;
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

        if (pathInfo == null || pathInfo.equals("/")) {
            List<MealDTO> meals = mealService.getAllMeals();
            out.print(gson.toJson(meals));
        } else if (pathInfo.startsWith("/user/")) {

            try {
                int userId = Integer.parseInt(pathInfo.substring(6));
                List<MealDTO> meals = mealService.getMealsByUserId(userId);
                out.print(gson.toJson(meals));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid user ID format\"}");
            }
        } else {

            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                MealDTO meal = mealService.getMeal(id);
                if (meal != null) {
                    out.print(gson.toJson(meal));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Meal not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid meal ID format\"}");
            }
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

        BufferedReader reader = request.getReader();
        MealDTO mealDTO = gson.fromJson(reader, MealDTO.class);

        mealService.createMeal(mealDTO);
        response.setStatus(HttpServletResponse.SC_CREATED);
        out.print("{\"message\": \"Meal created successfully\"}");
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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Meal ID is required\"}");
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BufferedReader reader = request.getReader();
            MealDTO mealDTO = gson.fromJson(reader, MealDTO.class);

            mealService.updateMeal(id, mealDTO);
            out.print("{\"message\": \"Meal updated successfully\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid meal ID format\"}");
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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Meal ID is required\"}");
            out.flush();
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            mealService.deleteMeal(id);
            out.print("{\"message\": \"Meal deleted successfully\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid meal ID format\"}");
        }
        out.flush();
    }
}
