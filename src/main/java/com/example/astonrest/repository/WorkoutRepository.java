package com.example.astonrest.repository;

import com.example.astonrest.entity.Workout;
import com.example.astonrest.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutRepository {

    // Константы для SQL запросов
    private static final String SQL_INSERT_WORKOUT = "INSERT INTO workouts (type, duration, calories_burned, user_id) VALUES (?, ?, ?, ?)";
    private static final String SQL_SELECT_WORKOUT_BY_ID = "SELECT * FROM workouts WHERE id = ?";
    private static final String SQL_SELECT_ALL_WORKOUTS = "SELECT * FROM workouts";
    private static final String SQL_UPDATE_WORKOUT = "UPDATE workouts SET type = ?, duration = ?, calories_burned = ? WHERE id = ?";
    private static final String SQL_DELETE_WORKOUT = "DELETE FROM workouts WHERE id = ?";
    private static final String SQL_SELECT_WORKOUTS_BY_USER_ID = "SELECT * FROM workouts WHERE user_id = ?";

    /**
     * Сохраняет новую тренировку в базе данных.
     */
    public void save(Workout workout) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_WORKOUT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, workout.getType());
            preparedStatement.setInt(2, workout.getDuration());
            preparedStatement.setInt(3, workout.getCaloriesBurned());
            preparedStatement.setInt(4, workout.getUserId());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                workout.setId(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Получает тренировку по ID.
     *
     * @param id ID тренировки
     * @return объект Workout или null, если не найден
     */
    public Workout findWorkoutById(int id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_WORKOUT_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Workout(
                        resultSet.getInt("id"),
                        resultSet.getString("type"),
                        resultSet.getInt("duration"),
                        resultSet.getInt("calories_burned"),
                        resultSet.getInt("user_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Возвращает список всех тренировок.
     *
     * @return список Workout
     */
    public List<Workout> findAllWorkouts() {
        List<Workout> workouts = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL_WORKOUTS)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                workouts.add(new Workout(
                        resultSet.getInt("id"),
                        resultSet.getString("type"),
                        resultSet.getInt("duration"),
                        resultSet.getInt("calories_burned"),
                        resultSet.getInt("user_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workouts;
    }


    /**
     * Обновляет данные тренировки.
     *
     * @param workout объект Workout с обновлёнными данными
     */
    public void update(Workout workout) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_WORKOUT)) {
            preparedStatement.setString(1, workout.getType());
            preparedStatement.setInt(2, workout.getDuration());
            preparedStatement.setInt(3, workout.getCaloriesBurned());
            preparedStatement.setInt(4, workout.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Удаляет тренировку по ID.
     *
     * @param id ID тренировки
     */
    public void delete(int id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_WORKOUT)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает список тренировок для конкретного пользователя.
     *
     * @param userId ID пользователя
     * @return список Workout
     */
    public List<Workout> findWorkoutsByUserId(int userId) {
        List<Workout> workouts = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_WORKOUTS_BY_USER_ID)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                workouts.add(new Workout(
                        resultSet.getInt("id"),
                        resultSet.getString("type"),
                        resultSet.getInt("duration"),
                        resultSet.getInt("calories_burned"),
                        resultSet.getInt("user_id"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workouts;
    }
}
