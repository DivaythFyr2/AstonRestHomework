package com.example.astonrest.exception;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ExceptionHandler {
    private static final Gson gson = new Gson();

    public static void handleException(HttpServletResponse response, Exception e, int statusCode) throws IOException {
        System.out.println("ERROR: " + e.getMessage());

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(new ErrorResponse(e.getMessage())));
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
