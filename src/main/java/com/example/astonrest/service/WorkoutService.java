package com.example.astonrest.service;

import com.example.astonrest.dto.WorkoutDTO;
import com.example.astonrest.entity.Workout;
import com.example.astonrest.exception.NotFoundException;
import com.example.astonrest.mapper.WorkoutMapper;
import com.example.astonrest.repository.UserRepository;
import com.example.astonrest.repository.WorkoutRepository;
import com.example.astonrest.util.WorkoutValidator;

import java.util.List;
import java.util.stream.Collectors;

public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    public WorkoutService(WorkoutRepository workoutRepository, UserRepository userRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
    }

    /**
     * Создаёт новую тренировку, автоматически рассчитывая `caloriesBurned`.
     *
     * @param workoutDTO DTO тренировки
     * @param userId     ID пользователя
     */
    public void createWorkoutForUser(WorkoutDTO workoutDTO, int userId) {
        if(!userRepository.doesUserExist(userId)) {
            throw new NotFoundException("User with ID " + userId + " does not exist.");
        }
        WorkoutValidator.validate(workoutDTO);
        int caloriesBurned = calculateCalories(workoutDTO.getType(), workoutDTO.getDuration());

        Workout workout = new Workout(0, workoutDTO.getType(), workoutDTO.getDuration(), caloriesBurned, userId);
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

            workoutRepository.update(existingWorkout);
        }
    }

    /**
     * Удаляет тренировку по ID.
     *
     * @param id ID тренировки
     */
    public void deleteWorkout(int id) {
        WorkoutDTO workout = getWorkoutById(id);
        if(workout == null) {
            throw new NotFoundException("Workout with ID " + id + " not found.");
        }
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
                return duration * 7;   // Велосипед сжигает примерно - 7 калорий/мин
            case "swimming":
                return duration * 8;  // Плавание сжигает примерно - 8 калорий/мин
            case "yoga":
                return duration * 4;      // Йога сжигает примерно - 4 калории/мин
            default:
                return duration * 5;          // Остальные тренировки - примерно 5 калорий/мин
        }
    }
}
