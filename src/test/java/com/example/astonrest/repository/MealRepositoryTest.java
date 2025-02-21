package com.example.astonrest.repository;

import com.example.astonrest.entity.Meal;
import com.example.astonrest.util.DatabaseUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealRepositoryTest {
    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    private MealRepository mealRepository;
    private MockedStatic<DatabaseUtil> mockedDatabaseUtil;

    private static final String SQL_INSERT_MEAL = "INSERT INTO meals (name, calories) VALUES (?, ?)";
    private static final String SQL_SELECT_MEAL_BY_ID = "SELECT * FROM meals WHERE id = ?";
    private static final String SQL_SELECT_ALL_MEALS = "SELECT * FROM meals";
    private static final String SQL_UPDATE_MEAL = "UPDATE meals SET name = ?, calories = ? WHERE id = ?";
    private static final String SQL_DELETE_MEAL = "DELETE FROM meals WHERE id = ?";
    private static final String SQL_SELECT_MEALS_BY_USER_ID =
            "SELECT m.* FROM meals m " +
                    "JOIN user_meals um ON m.id = um.meal_id " +
                    "WHERE um.user_id = ?";

    private static final List<Meal> EXPECTED_MEALS = List.of(
            new Meal(1, "Pasta", 500, List.of(1, 2)),
            new Meal(2, "Salad", 200, List.of(1))
    );

    @BeforeEach
    void setUp() throws SQLException {
        mealRepository = new MealRepository();

        mockedDatabaseUtil = mockStatic(DatabaseUtil.class);
        mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        lenient().when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        lenient().when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        lenient().when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        lenient().when(mockResultSet.next()).thenReturn(true);
        lenient().when(mockResultSet.getInt(1)).thenReturn(1);
    }

    @AfterEach
    void tearDown() {
        mockedDatabaseUtil.close();
    }

    @Test
    void testSaveMeal() throws SQLException {
        Meal expectedMeal = EXPECTED_MEALS.get(0);

        mealRepository.save(expectedMeal);

        verify(mockPreparedStatement, times(1)).executeUpdate();
        verify(mockPreparedStatement, times(1)).getGeneratedKeys();
        assertEquals(1, expectedMeal.getId());
    }

    @Test
    void testFindMealById() throws SQLException {
        Meal expectedMeal = EXPECTED_MEALS.get(0);

        when(mockConnection.prepareStatement(SQL_SELECT_MEAL_BY_ID)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(expectedMeal.getId());
        when(mockResultSet.getString("name")).thenReturn(expectedMeal.getName());
        when(mockResultSet.getInt("calories")).thenReturn(expectedMeal.getCalories());

        Meal actualMeal = mealRepository.findMealById(1);

        assertNotNull(actualMeal);
        assertEquals(expectedMeal.getId(), actualMeal.getId());
        assertEquals(expectedMeal.getName(), actualMeal.getName());
        assertEquals(expectedMeal.getCalories(), actualMeal.getCalories());

        verify(mockConnection, times(1)).prepareStatement(SQL_SELECT_MEAL_BY_ID);
        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testFindAllMeals() throws SQLException {
        when(mockConnection.prepareStatement(SQL_SELECT_ALL_MEALS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);  // два результата, затем false
        when(mockResultSet.getInt("id")).thenReturn(EXPECTED_MEALS.get(0).getId(), EXPECTED_MEALS.get(1).getId());
        when(mockResultSet.getString("name")).thenReturn(EXPECTED_MEALS.get(0).getName(), EXPECTED_MEALS.get(1).getName());
        when(mockResultSet.getInt("calories")).thenReturn(EXPECTED_MEALS.get(0).getCalories(), EXPECTED_MEALS.get(1).getCalories());

        List<Meal> actualMeals = mealRepository.findAllMeals();

        assertNotNull(actualMeals);
        assertEquals(EXPECTED_MEALS.size(), actualMeals.size());
        assertEquals(EXPECTED_MEALS.get(0).getName(), actualMeals.get(0).getName());
        assertEquals(EXPECTED_MEALS.get(1).getName(), actualMeals.get(1).getName());

        verify(mockConnection, times(1)).prepareStatement(SQL_SELECT_ALL_MEALS);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdateMeal() throws SQLException {
        Meal updatedMeal = EXPECTED_MEALS.get(0);
        updatedMeal.setName("Updated Pasta");

        when(mockConnection.prepareStatement(SQL_UPDATE_MEAL)).thenReturn(mockPreparedStatement);

        mealRepository.update(updatedMeal);

        verify(mockPreparedStatement, times(1)).setString(1, updatedMeal.getName());
        verify(mockPreparedStatement, times(1)).setInt(2, updatedMeal.getCalories());
        verify(mockPreparedStatement, times(1)).setInt(3, updatedMeal.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteMeal() throws SQLException {
        when(mockConnection.prepareStatement(SQL_DELETE_MEAL)).thenReturn(mockPreparedStatement);

        mealRepository.delete(1);

        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testFindMealsByUserId() throws SQLException {
        int userId = 2;

        when(mockConnection.prepareStatement(SQL_SELECT_MEALS_BY_USER_ID)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(EXPECTED_MEALS.get(0).getId());
        when(mockResultSet.getString("name")).thenReturn(EXPECTED_MEALS.get(0).getName());
        when(mockResultSet.getInt("calories")).thenReturn(EXPECTED_MEALS.get(0).getCalories());

        List<Meal> actualMeals = mealRepository.findMealsByUserId(userId);

        assertNotNull(actualMeals);
        assertEquals(1, actualMeals.size());
        assertEquals(EXPECTED_MEALS.get(0).getName(), actualMeals.get(0).getName());

        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(2)).next();
    }
}
