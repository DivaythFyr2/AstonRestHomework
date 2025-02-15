package com.example.astonrest.config.repository;

import com.example.astonrest.config.entity.Meal;
import com.example.astonrest.config.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MealRepository {

    // Константы для SQL запросов
    private static final String SQL_INSERT_MEAL = "INSERT INTO meals (name, calories) VALUES (?, ?)";
    private static final String SQL_SELECT_MEAL_BY_ID = "SELECT * FROM meals WHERE id = ?";
    private static final String SQL_SELECT_ALL_MEALS = "SELECT * FROM meals";
    private static final String SQL_UPDATE_MEAL = "UPDATE meals SET name = ?, calories = ? WHERE id = ?";
    private static final String SQL_DELETE_MEAL = "DELETE FROM meals WHERE id = ?";
    private static final String SQL_SELECT_MEALS_BY_USER_ID =
            "SELECT m.* FROM meals m " +
                    "JOIN user_meals um ON m.id = um.meal_id " +
                    "WHERE um.user_id = ?";

    /**
     * Сохраняет новый приём пищи в базе данных.
     * @param meal объект Meal
     */
    public void save(Meal meal) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_MEAL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, meal.getName());
            preparedStatement.setInt(2, meal.getCalories());

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                meal.setId(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает информацию о приёме пищи по ID.
     * @param id ID приёма пищи
     * @return объект Meal или null, если не найден
     */
    public Meal findMealById(int id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MEAL_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Meal(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("calories"),
                        new ArrayList<>()
                );

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Возвращает список всех приёмов пищи.
     * @return список Meal
     */
    public List<Meal> findAllMeals() {
        List<Meal> meals = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL_MEALS)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                meals.add(new Meal(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("calories"),
                        new ArrayList<>()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meals;
    }

    /**
     * Обновляет информацию о приёме пищи.
     * @param meal объект Meal с обновлёнными данными
     */
    public void update(Meal meal) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_MEAL)) {
            preparedStatement.setString(1, meal.getName());
            preparedStatement.setInt(2, meal.getCalories());
            preparedStatement.setInt(3, meal.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Удаляет приём пищи по ID.
     * @param id ID приёма пищи
     */
    public void delete(int id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_MEAL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает список приёмов пищи для конкретного пользователя.
     * @param userId ID пользователя
     * @return список Meal
     */
    public List<Meal> findMealsByUserId(int userId) {
        List<Meal> meals = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MEALS_BY_USER_ID)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                meals.add(new Meal(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("calories"),
                        new ArrayList<>()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meals;
    }
}
