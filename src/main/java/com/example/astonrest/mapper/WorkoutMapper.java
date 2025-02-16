package com.example.astonrest.mapper;

import com.example.astonrest.dto.WorkoutDTO;
import com.example.astonrest.entity.Workout;

public class WorkoutMapper {
    /**
     * Преобразует сущность Workout в DTO.
     *
     * @param workout объект Workout
     * @return объект WorkoutDTO без ID и userId
     */
    public static WorkoutDTO toDTO(Workout workout) {
        return new WorkoutDTO(workout.getUserId(), workout.getType(), workout.getDuration());
    }

    /**
     * Преобразует DTO в сущность Workout.
     * ID и userId устанавливаются на 0, так как они задаются в базе.
     *
     * @param workoutDTO объект WorkoutDTO
     * @return объект Workout
     */
    public static Workout toEntity(WorkoutDTO workoutDTO) {
        return new Workout(0, 0, workoutDTO.getType(), workoutDTO.getDuration(), 0);
    }
}
