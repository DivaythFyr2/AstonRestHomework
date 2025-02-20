package com.example.astonrest.service;

import com.example.astonrest.dto.UserDTO;
import com.example.astonrest.entity.User;
import com.example.astonrest.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private static List<User> users;
    private static List<UserDTO> userDTOs;

    @BeforeAll
    static void setUp() {
        users = Arrays.asList(
                new User(1, "Jakub", 27, 72, 180, new ArrayList<>(), new ArrayList<>()),
                new User(2, "Novak", 38, 75, 189, new ArrayList<>(), new ArrayList<>())
        );
        userDTOs = users.stream()
                .map(user -> new UserDTO(user.getName(), user.getAge(), user.getWeight(), user.getHeight()))
                .collect(Collectors.toList());
    }

    @BeforeEach
    void init() {
        userService = new UserService(userRepository);
    }

    @Test
    void testCreateUser() {
        // Создаем DTO для пользователя
        UserDTO userDTO = userDTOs.get(0);

        // Мокаем ожидаемого пользователя, который будет сохранен в репозитории
        User expectedUser = new User(0, userDTO.getName(), userDTO.getAge(), userDTO.getWeight(), userDTO.getHeight(), new ArrayList<>(), new ArrayList<>());

        // Мокаем поведение репозитория, проверяем, что метод save был вызван с ожидаемым пользователем
        doNothing().when(userRepository).save(any(User.class));  // Мокаем метод save

        // Выполняем создание пользователя через сервис
        userService.createUser(userDTO);

        // Используем ArgumentCaptor для захвата аргумента, передаваемого в save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture()); // Проверка, что метод save был вызван

        // Проверяем, что сохраненный пользователь имеет ожидаемые значения (кроме ID)
        User capturedUser = userCaptor.getValue();
        assertEquals(expectedUser.getName(), capturedUser.getName());
        assertEquals(expectedUser.getAge(), capturedUser.getAge());
        assertEquals(expectedUser.getWeight(), capturedUser.getWeight());
        assertEquals(expectedUser.getHeight(), capturedUser.getHeight());
    }


    @Test
    void testGetUserById() {
        User expectedUser = users.get(0); // Берем первого пользователя из списка
        when(userRepository.findUserById(1)).thenReturn(expectedUser);

        UserDTO actualUserDto = userService.getUserById(1);

        assertNotNull(actualUserDto);
        assertEquals(actualUserDto.getName(), expectedUser.getName());
        assertEquals(actualUserDto.getAge(), expectedUser.getAge());
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAllUsers()).thenReturn(users);

        List<UserDTO> actualUserDto = userService.getAllUsers();

        assertNotNull(actualUserDto);
        assertEquals(userDTOs.size(), actualUserDto.size());
        assertEquals(userDTOs.get(0).getName(), actualUserDto.get(0).getName());
        assertEquals(userDTOs.get(0).getAge(), actualUserDto.get(0).getAge());
        assertEquals(userDTOs.get(1).getName(), actualUserDto.get(1).getName());
        assertEquals(userDTOs.get(1).getAge(), actualUserDto.get(1).getAge());
    }

    @Test
    void testUpdateUser() {
        UserDTO updatedUserDTO = userDTOs.get(1);
        User existingUser = new User(2, updatedUserDTO.getName(), updatedUserDTO.getAge(), updatedUserDTO.getWeight(), updatedUserDTO.getHeight(),
                new ArrayList<>(), new ArrayList<>());

        when(userRepository.findUserById(2)).thenReturn(existingUser);
        doNothing().when(userRepository).update(existingUser);

        userService.updateUser(2, updatedUserDTO);

        verify(userRepository, times(1)).update(existingUser);
    }

    @Test
    void testDeleteUser() {
        UserDTO userDTO = userDTOs.get(0);
        User existingUser = new User(1, userDTO.getName(), userDTO.getAge(), userDTO.getWeight(), userDTO.getHeight(), new ArrayList<>(), new ArrayList<>());

        when(userRepository.findUserById(1)).thenReturn(existingUser);
        doNothing().when(userRepository).delete(1);

        userService.deleteUser(1);

        verify(userRepository, times(1)).delete(1);
    }
}