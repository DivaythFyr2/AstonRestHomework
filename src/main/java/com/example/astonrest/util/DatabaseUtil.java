package com.example.astonrest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import java.util.Properties;

public class DatabaseUtil {
    private static final String driver;
    private static final String url;
    private static final String username;
    private static final String password;

    static {
        Properties prop = new Properties();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(DatabaseUtil.class.getClassLoader().getResourceAsStream("database.properties"))))) {

            prop.load(reader);

            driver = prop.getProperty("driver");
            url = prop.getProperty("url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");

            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации базы данных");
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка подключения к базе данных");
        }
    }
}
