package com.example.astonrest.util;

import com.example.astonrest.dto.UserDTO;
import com.example.astonrest.exception.BadRequestException;

import java.util.regex.Pattern;

public class UserValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zА-Яа-яЁё\\s-]+$");

    /**
     * Валидирует поля пользователя перед сохранением в базе данных.
     *
     * @param userDTO Объект DTO для пользователя, который нужно валидировать.
     * @throws BadRequestException Если одно из полей не прошло валидацию.
     */
    public static void validate(UserDTO userDTO) {
        if (userDTO.getName() == null || userDTO.getName().trim().isEmpty()) {
            throw new BadRequestException("User name cannot be empty.");
        }
        if (!NAME_PATTERN.matcher(userDTO.getName()).matches()) {
            throw new BadRequestException("User name must contain only letters and spaces.");
        }

        if (userDTO.getAge() <= 0) {
            throw new BadRequestException("Age must be a positive number.");
        }

        if (userDTO.getHeight() <= 0) {
            throw new BadRequestException("Height must be a positive number.");
        }

        if (userDTO.getWeight() <= 0) {
            throw new BadRequestException("Weight must be a positive number.");
        }
    }
}
