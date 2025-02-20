package com.example.astonrest.controller;

import com.example.astonrest.dto.MealDTO;
import com.example.astonrest.dto.UserDTO;
import com.example.astonrest.dto.WorkoutDTO;
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


public class UserServlet extends HttpServlet {
    private UserService userService;
    private WorkoutService workoutService;
    private MealService mealService;
    private final Gson gson = new Gson();
    private static final String CONTENT_TYPE = "application/json";

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
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<UserDTO> users = userService.getAllUsers();
                out.print(gson.toJson(users));
                response.setStatus(HttpServletResponse.SC_OK); //
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int id = Integer.parseInt(pathParts[1]);
                    UserDTO user = userService.getUserById(id);
                    if (user == null) {
                        throw new NotFoundException("User not found");
                    }
                    out.print(gson.toJson(user));
                    response.setStatus(HttpServletResponse.SC_OK); //
                } else if (pathParts.length == 3) {
                    int userId = Integer.parseInt(pathParts[1]);
                    if ("workouts".equals(pathParts[2])) {
                        List<WorkoutDTO> userWorkouts = workoutService.getWorkoutsByUserId(userId);
                        out.print(gson.toJson(userWorkouts));
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else if ("meals".equals(pathParts[2])) {
                        List<MealDTO> userMeals = mealService.getMealsByUserId(userId);
                        out.print(gson.toJson(userMeals));
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        throw new BadRequestException("Invalid request format.");
                    }
                } else {
                    throw new BadRequestException("Invalid request format.");
                }
            }
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid user ID format"),
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
     * Создаёт нового пользователя (POST /users)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        try {
            BufferedReader reader = request.getReader();
            UserDTO userDTO = gson.fromJson(reader, UserDTO.class);

            userService.createUser(userDTO);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"message\": \"User created successfully\"}");
        } catch (BadRequestException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException("Internal Server Error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.flush();
    }

    /**
     * Обновляет пользователя по ID (PUT /users/{id})
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            ExceptionHandler.handleException(response, new BadRequestException("User ID is required"),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            BufferedReader reader = request.getReader();
            UserDTO userDTO = gson.fromJson(reader, UserDTO.class);

            userService.updateUser(id, userDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"message\": \"User updated successfully\"}");
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid user ID format"),
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
     * Удаляет пользователя по ID (DELETE /users/{id})
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            ExceptionHandler.handleException(response, new BadRequestException("User ID is required."),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            userService.deleteUser(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            out.print("{\"message\": \"User deleted successfully\"}");
        } catch (NumberFormatException e) {
            ExceptionHandler.handleException(response, new BadRequestException("Invalid user ID format"),
                    HttpServletResponse.SC_BAD_REQUEST);
        } catch (NotFoundException | BadRequestException e) {
            ExceptionHandler.handleException(response, e, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ExceptionHandler.handleException(response, new CustomException("Internal Server Error"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        out.flush();
    }
}
