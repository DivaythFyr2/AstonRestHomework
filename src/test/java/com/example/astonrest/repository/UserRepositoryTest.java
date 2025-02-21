package com.example.astonrest.repository;

import com.example.astonrest.entity.User;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {
    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    private UserRepository userRepository;
    private MockedStatic<DatabaseUtil> mockedDatabaseUtil;

    private static final String SQL_INSERT_USER = "INSERT INTO users (name, age, weight, height) VALUES (?,?,?,?)";
    private static final String SQL_SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String SQL_SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SQL_UPDATE_USER = "UPDATE users SET name = ?, age = ?, weight = ?, height = ? WHERE id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE id = ?";

    private static final List<User> EXPECTED_USERS = List.of(
            new User(1, "Alice", 25, 60.5, 165, new ArrayList<>(), new ArrayList<>()),
            new User(2, "Bob", 32, 82.5, 185, new ArrayList<>(), new ArrayList<>())
    );

    @BeforeEach
    void setUp() throws SQLException {
        userRepository = new UserRepository();

        // Мокаем статический метод DatabaseUtil.getConnection()
        mockedDatabaseUtil = mockStatic(DatabaseUtil.class);
        mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

        // Общие мокирования для всех тестов
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
    void testSaveUser() throws SQLException {
        User expectedUser = EXPECTED_USERS.get(0);

        userRepository.save(expectedUser);

        verify(mockPreparedStatement, times(1)).executeUpdate();
        verify(mockPreparedStatement, times(1)).getGeneratedKeys();
        assertEquals(1, expectedUser.getId());
    }

    @Test
    void testFindUserById() throws SQLException {
        User expectedUser = EXPECTED_USERS.get(0);

        // Мокаем поведение запроса
        when(mockConnection.prepareStatement(SQL_SELECT_USER_BY_ID)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(expectedUser.getId());
        when(mockResultSet.getString("name")).thenReturn(expectedUser.getName());
        when(mockResultSet.getInt("age")).thenReturn(expectedUser.getAge());
        when(mockResultSet.getDouble("weight")).thenReturn(expectedUser.getWeight());
        when(mockResultSet.getDouble("height")).thenReturn(expectedUser.getHeight());

        User actualUser = userRepository.findUserById(1);

        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getAge(), actualUser.getAge());
        assertEquals(expectedUser.getWeight(), actualUser.getWeight());
        assertEquals(expectedUser.getHeight(), actualUser.getHeight());

        verify(mockConnection, times(1)).prepareStatement(SQL_SELECT_USER_BY_ID);
        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testFindAllUsers() throws SQLException {
        when(mockConnection.prepareStatement(SQL_SELECT_ALL_USERS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(EXPECTED_USERS.get(0).getId(), EXPECTED_USERS.get(1).getId());
        when(mockResultSet.getString("name")).thenReturn(EXPECTED_USERS.get(0).getName(), EXPECTED_USERS.get(1).getName());
        when(mockResultSet.getInt("age")).thenReturn(EXPECTED_USERS.get(0).getAge(), EXPECTED_USERS.get(1).getAge());
        when(mockResultSet.getDouble("weight")).thenReturn(EXPECTED_USERS.get(0).getWeight(), EXPECTED_USERS.get(1).getWeight());
        when(mockResultSet.getDouble("height")).thenReturn(EXPECTED_USERS.get(0).getHeight(), EXPECTED_USERS.get(1).getHeight());

        List<User> actualUsers = userRepository.findAllUsers();

        assertNotNull(actualUsers);
        assertEquals(EXPECTED_USERS.size(), actualUsers.size());
        assertEquals(EXPECTED_USERS.get(0).getName(), actualUsers.get(0).getName());
        assertEquals(EXPECTED_USERS.get(0).getAge(), actualUsers.get(0).getAge());
        assertEquals(EXPECTED_USERS.get(1).getName(), actualUsers.get(1).getName());
        assertEquals(EXPECTED_USERS.get(1).getAge(), actualUsers.get(1).getAge());

        verify(mockConnection, times(1)).prepareStatement(SQL_SELECT_ALL_USERS);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdateUser() throws SQLException {
        User expectedUser = EXPECTED_USERS.get(0);

        when(mockConnection.prepareStatement(SQL_UPDATE_USER)).thenReturn(mockPreparedStatement);

        userRepository.update(expectedUser);

        verify(mockPreparedStatement, times(1)).setString(1, expectedUser.getName());
        verify(mockPreparedStatement, times(1)).setInt(2, expectedUser.getAge());
        verify(mockPreparedStatement, times(1)).setDouble(3, expectedUser.getWeight());
        verify(mockPreparedStatement, times(1)).setDouble(4, expectedUser.getHeight());
        verify(mockPreparedStatement, times(1)).setInt(5, expectedUser.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteUser() throws SQLException {
        when(mockConnection.prepareStatement(SQL_DELETE_USER)).thenReturn(mockPreparedStatement);

        userRepository.delete(1);

        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDoesUserExist() throws SQLException {
        when(mockConnection.prepareStatement("SELECT COUNT(*) FROM users WHERE id = ?")).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        boolean exists = userRepository.doesUserExist(1);

        assertTrue(exists);

        verify(mockPreparedStatement, times(1)).setInt(1, 1);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }
}