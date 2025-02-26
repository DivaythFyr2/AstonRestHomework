package com.example.astonrest.config;

import com.example.astonrest.controller.MealServlet;
import com.example.astonrest.controller.UserServlet;
import com.example.astonrest.controller.WorkoutServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.annotation.WebListener;

/**
 * Конфигурационный класс для регистрации сервлетов в контексте приложения.
 * Автоматически выполняет регистрацию сервлетов при запуске веб-приложения.
 */
@WebListener
public class ServletConfig implements ServletContextListener {

    /**
     * Вызывается при инициализации контекста сервлетов.
     * Регистрирует сервлеты пользователей, приемов пищи и тренировок.
     *
     * @param servletContextEvent событие инициализации контекста сервлетов
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        ServletRegistration.Dynamic userServlet = servletContext.addServlet("UserServlet", new UserServlet());
        userServlet.addMapping("/users/*");
        userServlet.setLoadOnStartup(1);

        ServletRegistration.Dynamic mealServlet = servletContext.addServlet("MealServlet", new MealServlet());
        mealServlet.addMapping("/meals/*");
        mealServlet.setLoadOnStartup(1);

        ServletRegistration.Dynamic workoutServlet = servletContext.addServlet("WorkoutServlet", new WorkoutServlet());
        workoutServlet.addMapping("/workouts/*");
        workoutServlet.setLoadOnStartup(1);
    }
}

