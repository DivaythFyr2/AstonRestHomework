package com.example.astonrest.service;

import com.example.astonrest.dto.UserDTO;
import com.example.astonrest.entity.User;
import com.example.astonrest.exception.NotFoundException;
import com.example.astonrest.mapper.UserMapper;
import com.example.astonrest.repository.UserRepository;
import com.example.astonrest.util.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создаёт нового пользователя.
     * @param userDTO DTO пользователя
     */
    public void createUser(UserDTO userDTO) {
        UserValidator.validate(userDTO);
        User user = UserMapper.toEntity(userDTO);
        userRepository.save(user);
    }

    /**
     * Получает пользователя по ID.
     * @param id ID пользователя
     * @return DTO пользователя
     */
    public UserDTO getUserById(int id) {
        User user = userRepository.findUserById(id);
        return (user != null) ? UserMapper.toDTO(user) : null;
    }

    /**
     * Получает список всех пользователей.
     * @return Список пользователей в формате DTO
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsers()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет данные пользователя.
     * @param id ID пользователя
     * @param userDTO Обновлённые данные
     */
    public void updateUser(int id, UserDTO userDTO) {
        User existingUser = userRepository.findUserById(id);
        if(existingUser != null) {
            existingUser.setName(userDTO.getName());
            existingUser.setAge(userDTO.getAge());
            existingUser.setWeight(userDTO.getWeight());
            existingUser.setHeight(userDTO.getHeight());

            userRepository.update(existingUser);
        }
    }

    /**
     * Удаляет пользователя по ID.
     * @param id ID пользователя
     */
    public void deleteUser(int id) {
        UserDTO user = getUserById(id);
        if(user == null) {
            throw new NotFoundException("User with ID " + id + " not found.");
        }
        userRepository.delete(id);
    }
}
