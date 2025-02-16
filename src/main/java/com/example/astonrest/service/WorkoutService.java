package com.example.astonrest.service;

import com.example.astonrest.dto.WorkoutDTO;
import com.example.astonrest.entity.Workout;
import com.example.astonrest.mapper.WorkoutMapper;
import com.example.astonrest.repository.WorkoutRepository;

import java.util.List;
import java.util.stream.Collectors;

public class WorkoutService {
    private final WorkoutRepository workoutRepository;

    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    /**
     * Создаёт новую тренировку, автоматически рассчитывая `caloriesBurned`.
     *
     * @param workoutDTO DTO тренировки
     * @param userId     ID пользователя
     */
    public void createWorkout(WorkoutDTO workoutDTO, int userId) {
        int caloriesBurned = calculateCalories(workoutDTO.getType(), workoutDTO.getDuration());
        Workout workout = new Workout(0, userId, workoutDTO.getType(), workoutDTO.getDuration(), caloriesBurned);
        workoutRepository.save(workout);
    }

    /**
     * Получает тренировку по ID.
     *
     * @param id ID тренировки
     * @return DTO тренировки
     */
    public WorkoutDTO getWorkoutById(int id) {
        Workout workout = workoutRepository.findWorkoutById(id);
        return (workout != null) ? WorkoutMapper.toDTO(workout) : null;
    }

    /**
     * Получает список всех тренировок.
     *
     * @return список тренировок в формате DTO
     */
    public List<WorkoutDTO> getAllWorkouts() {
        return workoutRepository.findAllWorkouts()
                .stream()
                .map(WorkoutMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет данные тренировки.
     *
     * @param id         ID тренировки
     * @param workoutDTO Обновлённые данные
     */
    public void updateWorkout(int id, WorkoutDTO workoutDTO) {
        Workout existingWorkout = workoutRepository.findWorkoutById(id);
        if (existingWorkout != null) {
            existingWorkout.setType(workoutDTO.getType());
            existingWorkout.setDuration(workoutDTO.getDuration());
            existingWorkout.setCaloriesBurned(calculateCalories(workoutDTO.getType(), workoutDTO.getDuration()));
        }
    }

    /**
     * Удаляет тренировку по ID.
     *
     * @param id ID тренировки
     */
    public void deleteWorkout(int id) {
        workoutRepository.delete(id);
    }

    /**
     * Получает список тренировок для конкретного пользователя.
     *
     * @param userId ID пользователя
     * @return список тренировок в формате DTO
     */
    public List<WorkoutDTO> getWorkoutsByUserId(int userId) {
        return workoutRepository.findWorkoutsByUserId(userId)
                .stream()
                .map(WorkoutMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Рассчитывает количество сожжённых калорий на основе типа тренировки и длительности.
     *
     * @param type     Тип тренировки (бег, велосипед, плавание и т. д.)
     * @param duration Длительность тренировки (в минутах)
     * @return Количество сожжённых калорий
     */
    private int calculateCalories(String type, int duration) {
        switch (type.toLowerCase()) {
            case "running":
                return duration * 12;  // Бег сжигает примерно 12 калорий/мин
            case "cycling":
                return duration * 7;   // Велосипед сжигает примерно - 6 калорий/мин
            case "swimming":
                return duration * 8;  // Плавание сжигает примерно - 8 калорий/мин
            case "yoga":
                return duration * 4;      // Йога сжигает примерно - 4 калории/мин
            default:
                return duration * 5;          // Остальные тренировки - примерно 5 калорий/мин
        }
    }
}
