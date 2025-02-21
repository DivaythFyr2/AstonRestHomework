package com.example.astonrest.repository;

import com.example.astonrest.entity.Workout;
import com.example.astonrest.util.DatabaseUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutRepositoryTest {
    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    private WorkoutRepository workoutRepository;
    private MockedStatic<DatabaseUtil> mockedDatabaseUtil;

    private static final String SQL_INSERT_WORKOUT = "INSERT INTO workouts (type, duration, calories_burned, user_id) VALUES (?, ?, ?, ?)";
    private static final String SQL_SELECT_WORKOUT_BY_ID = "SELECT * FROM workouts WHERE id = ?";
    private static final String SQL_SELECT_ALL_WORKOUTS = "SELECT * FROM workouts";
    private static final String SQL_UPDATE_WORKOUT = "UPDATE workouts SET type = ?, duration = ?, calories_burned = ? WHERE id = ?";
    private static final String SQL_DELETE_WORKOUT = "DELETE FROM workouts WHERE id = ?";
    private static final String SQL_SELECT_WORKOUTS_BY_USER_ID = "SELECT * FROM workouts WHERE user_id = ?";

    private static final List<Workout> EXPECTED_WORKOUTS = List.of(
            new Workout(1, "Running", 30, 360, 1),
            new Workout(2, "Cycling", 45, 315, 2)
    );

    @BeforeEach
    void setUp() throws SQLException {
        workoutRepository = new WorkoutRepository();

        mockedDatabaseUtil = mockStatic(DatabaseUtil.class);
        mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        lenient().when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        lenient().when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        lenient().when(mockResultSet.getInt(1)).thenReturn(1);
    }

    @AfterEach
    void tearDown() {
        mockedDatabaseUtil.close();
    }

    @Test
    void testSaveWorkout() throws SQLException {
        Workout expectedWorkout = EXPECTED_WORKOUTS.get(0);

        when(mockConnection.prepareStatement(SQL_INSERT_WORKOUT, Statement.RETURN_GENERATED_KEYS))
                .thenReturn(mockPreparedStatement);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(10);

        workoutRepository.save(expectedWorkout);

        verify(mockPreparedStatement).setString(1, expectedWorkout.getType());
        verify(mockPreparedStatement).setInt(2, expectedWorkout.getDuration());
        verify(mockPreparedStatement).setInt(3, expectedWorkout.getCaloriesBurned());
        verify(mockPreparedStatement).setInt(4, expectedWorkout.getUserId());

        verify(mockPreparedStatement, times(1)).executeUpdate();

        verify(mockPreparedStatement, times(1)).getGeneratedKeys();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(1)).getInt(1);

        assertEquals(10, expectedWorkout.getId());
    }

    @Test
    void testFindWorkoutById() throws SQLException {
        Workout expectedWorkout = EXPECTED_WORKOUTS.get(0);

        when(mockConnection.prepareStatement(SQL_SELECT_WORKOUT_BY_ID)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(expectedWorkout.getId());
        when(mockResultSet.getString("type")).thenReturn(expectedWorkout.getType());
        when(mockResultSet.getInt("duration")).thenReturn(expectedWorkout.getDuration());
        when(mockResultSet.getInt("calories_burned")).thenReturn(expectedWorkout.getCaloriesBurned());
        when(mockResultSet.getInt("user_id")).thenReturn(expectedWorkout.getUserId());

        Workout actualWorkout = workoutRepository.findWorkoutById(1);

        assertNotNull(actualWorkout);
        assertEquals(expectedWorkout.getId(), actualWorkout.getId());
        assertEquals(expectedWorkout.getType(), actualWorkout.getType());
        assertEquals(expectedWorkout.getDuration(), actualWorkout.getDuration());
        assertEquals(expectedWorkout.getCaloriesBurned(), actualWorkout.getCaloriesBurned());
        assertEquals(expectedWorkout.getUserId(), actualWorkout.getUserId());
    }

    @Test
    void testFindAllWorkouts() throws SQLException {
        when(mockConnection.prepareStatement(SQL_SELECT_ALL_WORKOUTS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("type")).thenReturn("Running", "Cycling");
        when(mockResultSet.getInt("duration")).thenReturn(30, 45);
        when(mockResultSet.getInt("calories_burned")).thenReturn(360, 315);
        when(mockResultSet.getInt("user_id")).thenReturn(1, 2);

        List<Workout> actualWorkouts = workoutRepository.findAllWorkouts();

        assertNotNull(actualWorkouts);
        assertEquals(2, actualWorkouts.size());
        assertEquals("Running", actualWorkouts.get(0).getType());
        assertEquals("Cycling", actualWorkouts.get(1).getType());

        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(3)).next();
    }

    @Test
    void testUpdateWorkout() throws SQLException {
        Workout updatedWorkout = EXPECTED_WORKOUTS.get(0);
        updatedWorkout.setType("Running Updated");

        when(mockConnection.prepareStatement(SQL_UPDATE_WORKOUT)).thenReturn(mockPreparedStatement);

        workoutRepository.update(updatedWorkout);

        verify(mockPreparedStatement, times(1)).setString(1, updatedWorkout.getType());
        verify(mockPreparedStatement, times(1)).setInt(2, updatedWorkout.getDuration());
        verify(mockPreparedStatement, times(1)).setInt(3, updatedWorkout.getCaloriesBurned());
        verify(mockPreparedStatement, times(1)).setInt(4, updatedWorkout.getId());

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteWorkout() throws SQLException {
        when(mockConnection.prepareStatement(SQL_DELETE_WORKOUT)).thenReturn(mockPreparedStatement);

        workoutRepository.delete(1);

        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testFindWorkoutsByUserId() throws SQLException {
        int userId = 1;

        when(mockConnection.prepareStatement(SQL_SELECT_WORKOUTS_BY_USER_ID)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("type")).thenReturn(EXPECTED_WORKOUTS.get(0).getType());
        when(mockResultSet.getInt("duration")).thenReturn(EXPECTED_WORKOUTS.get(0).getDuration());
        when(mockResultSet.getInt("calories_burned")).thenReturn(EXPECTED_WORKOUTS.get(0).getCaloriesBurned());
        when(mockResultSet.getInt("user_id")).thenReturn(1);

        List<Workout> actualWorkouts = workoutRepository.findWorkoutsByUserId(userId);

        assertNotNull(actualWorkouts);
        assertEquals(1, actualWorkouts.size());
        assertEquals(EXPECTED_WORKOUTS.get(0).getType(), actualWorkouts.get(0).getType());

        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(2)).next();
    }
}