package com.example.astonrest.config.mapper;

import com.example.astonrest.config.dto.UserDTO;
import com.example.astonrest.config.entity.User;

public class UserMapper {
    /**
     * Преобразует сущность User в DTO.
     *
     * @param user объект User
     * @return объект UserDTO без внутренних данных (ID, тренировки, еда)
     */
    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getName(), user.getAge(), user.getWeight(), user.getHeight());
    }

    /**
     * Преобразует DTO в сущность User.
     * ID устанавливается на 0, так как он будет сгенерирован в базе данных.
     *
     * @param userDTO объект UserDTO
     * @return объект User с пустыми списками тренировок и еды
     */
    public static User toEntity(UserDTO userDTO) {
        return new User(0, userDTO.getName(), userDTO.getAge(), userDTO.getWeight(), userDTO.getHeight(), null, null);
    }
}
