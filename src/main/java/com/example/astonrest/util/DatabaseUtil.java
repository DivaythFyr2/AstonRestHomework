package com.example.astonrest.util;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class DatabaseUtil {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

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

    /*public static void executeSQLFile(String filePath) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     Objects.requireNonNull(DatabaseUtil.class.getClassLoader().getResourceAsStream(filePath))))) {

            String sql = reader.lines().collect(Collectors.joining("\n"));
            stmt.execute(sql);
            System.out.println("Выполнен SQL-файл: " + filePath);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL-файла: " + filePath, e);
        }
    }*/
}
