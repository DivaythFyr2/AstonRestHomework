package com.example.astonrest.config;

import com.example.astonrest.controller.MealServlet;
import com.example.astonrest.controller.UserServlet;
import com.example.astonrest.controller.WorkoutServlet;
import com.example.astonrest.util.DatabaseUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ServletConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        //DatabaseUtil.executeSQLFile("src/main/resources/database/drop.sql");
        //DatabaseUtil.executeSQLFile("src/main/resources/database/schema.sql");
        //DatabaseUtil.executeSQLFile("src/main/resources/database/data.sql");

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

