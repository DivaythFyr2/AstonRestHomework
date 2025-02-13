package com.example.astonrest.config.repository;

import com.example.astonrest.config.entity.User;
import com.example.astonrest.config.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    // Константы для SQL запросов
    private static final String SQL_INSERT_USER = "INSERT INTO users (name, age, weight, height) VALUES (?,?,?,?)";
    private static final String SQL_SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String SQL_SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SQL_UPDATE_USER = "UPDATE users SET name = ?, age = ?, weight = ?, height = ? WHERE id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE id = ?";

    /**
     * Создаёт нового пользователя в базе данных.
     *
     * @param user объект User
     */
    public void save(User user) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setInt(2, user.getAge());
            preparedStatement.setDouble(3, user.getWeight());
            preparedStatement.setDouble(4, user.getHeight());

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает пользователя по ID.
     *
     * @param id ID пользователя
     * @return объект User или null, если не найден
     */
    public User findUserById(int id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getDouble("weight"),
                        resultSet.getDouble("height"),
                        new ArrayList<>(),
                        new ArrayList<>()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получает всех пользователей из базы данных.
     *
     * @return список пользователей
     */
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL_USERS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getDouble("weight"),
                        resultSet.getDouble("height"),
                        new ArrayList<>(),
                        new ArrayList<>()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param user объект User с обновлёнными данными
     */
    public void updateUser(User user) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_USER)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setInt(2, user.getAge());
            preparedStatement.setDouble(3, user.getWeight());
            preparedStatement.setDouble(4, user.getHeight());
            preparedStatement.setInt(5, user.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id ID пользователя
     */
    public void delete(int id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_USER)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
