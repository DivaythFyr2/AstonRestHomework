package com.example.astonrest.constants;

public class ApiConstants {

    private ApiConstants() {}

    public static final String CONTENT_TYPE = "application/json";
    public static final String CHARACTER_ENCODING = "UTF-8";

    // Пути
    public static final String ROOT_PATH = "/";
    public static final String USERS_PATH = "users";
    public static final String USERS_PATH_WITH_SLASH = "/users/";
    public static final String USER_PATH_WITH_SLASH = "/user/";
    public static final String MEALS_PATH = "meals";
    public static final String WORKOUTS_PATH = "workouts";
    public static final String WORKOUTS_USERS_PATH_WITH_SLASH = "/workouts/users/";

    // Сообщения об успешном создании/обновлении/удалении
    public static final String USER_CREATED_SUCCESSFULLY = "User created successfully";
    public static final String USER_UPDATED_SUCCESSFULLY = "User updated successfully";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";

    public static final String MEAL_CREATED_SUCCESSFULLY = "Meal created successfully";
    public static final String MEAL_UPDATED_SUCCESSFULLY = "Meal updated successfully";
    public static final String MEAL_DELETED_SUCCESSFULLY = "Meal deleted successfully";

    public static final String WORKOUT_CREATED_SUCCESSFULLY = "Workout created successfully";
    public static final String WORKOUT_UPDATED_SUCCESSFULLY = "Workout updated successfully";
    public static final String WORKOUT_DELETED_SUCCESSFULLY = "Workout deleted successfully";


    // Сообщения об ошибках
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ID_IS_REQUIRED = "User ID is required";
    public static final String MEAL_NOT_FOUND = "Meal not found";
    public static final String MEAL_ID_IS_REQUIRED = "Meal ID is required";
    public static final String WORKOUT_NOT_FOUND = "Workout not found";
    public static final String WORKOUT_ID_IS_REQUIRED = "Workout ID is required";
    public static final String INVALID_WORKOUT_REQUEST_FORMAT =
            "Invalid request format. Use /users/{id}/workouts or /workouts/users/{id}";


    public static final String INVALID_USER_ID = "Invalid user ID format";
    public static final String INVALID_MEAL_ID = "Invalid meal ID format";
    public static final String INVALID_WORKOUT_ID = "Invalid workout ID format";

    public static final String INVALID_REQUEST = "Invalid request format";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";

}
