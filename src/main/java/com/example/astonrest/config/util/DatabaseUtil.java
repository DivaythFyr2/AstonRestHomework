package com.example.astonrest.config.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseUtil {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("src/main/resources/database.properties"));

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
            return DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка подключения к базе данных");
        }
    }
}
