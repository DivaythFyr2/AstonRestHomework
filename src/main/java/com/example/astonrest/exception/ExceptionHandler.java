package com.example.astonrest.exception;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Класс для обработки исключений и возврата стандартного ответа с ошибкой.
 */
public class ExceptionHandler {
    private static final Gson gson = new Gson();

    /**
     * Обрабатывает исключение, устанавливая статус ответа и возвращая JSON с ошибкой.
     */
    public static void handleException(HttpServletResponse response, Exception e, int statusCode) throws IOException {
        System.out.println("ERROR: " + e.getMessage());

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(new ErrorResponse(e.getMessage())));
    }

    /**
     * Внутренний класс, представляющий JSON-объект для возврата ошибки.
     */
    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
