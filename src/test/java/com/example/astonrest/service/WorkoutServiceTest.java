package com.example.astonrest.service;

import com.example.astonrest.dto.WorkoutDTO;
import com.example.astonrest.entity.Workout;
import com.example.astonrest.repository.UserRepository;
import com.example.astonrest.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkoutService workoutService;

    private static List<Workout> workouts;
    private static List<WorkoutDTO> workoutDTOs;

    @BeforeAll
    static void setUp() {
        workouts = Arrays.asList(
                new Workout(1, "Running", 30, 360, 1),
                new Workout(2, "Cycling", 45, 315, 2)
        );
        workoutDTOs = workouts.stream()
                .map(workout -> new WorkoutDTO(workout.getType(), workout.getDuration(),workout.getCaloriesBurned(), workout.getUserId()))
                .collect(Collectors.toList());
    }

    @BeforeEach
    void init() {
        workoutService = new WorkoutService(workoutRepository, userRepository);
    }

    @Test
    void testCreateWorkoutForUser() {
        WorkoutDTO workoutDTO = workoutDTOs.get(0);
        Workout expectedWorkout = new Workout(0, workoutDTO.getType(), workoutDTO.getDuration(), workoutDTO.getCaloriesBurned(),
                workoutDTO.getUserId());

        // Мокаем существование пользователя
        when(userRepository.doesUserExist(workoutDTO.getUserId())).thenReturn(true);
        // Мокаем сохранение тренировки
        doNothing().when(workoutRepository).save(any(Workout.class));

        workoutService.createWorkoutForUser(workoutDTO, workoutDTO.getUserId());

        ArgumentCaptor<Workout> workoutCaptor = ArgumentCaptor.forClass(Workout.class);
        verify(workoutRepository, times(1)).save(workoutCaptor.capture());
        Workout capturedWorkout = workoutCaptor.getValue();

        assertEquals(expectedWorkout.getType(), capturedWorkout.getType());
        assertEquals(expectedWorkout.getDuration(), capturedWorkout.getDuration());
        assertEquals(expectedWorkout.getCaloriesBurned(), capturedWorkout.getCaloriesBurned());
    }

    @Test
    void testGetWorkoutById() {
        Workout expectedWorkout = workouts.get(0);
        when(workoutRepository.findWorkoutById(1)).thenReturn(expectedWorkout);

        WorkoutDTO actualWorkoutDTO = workoutService.getWorkoutById(1);

        assertNotNull(actualWorkoutDTO);
        assertEquals(expectedWorkout.getType(), actualWorkoutDTO.getType());
        assertEquals(expectedWorkout.getDuration(), actualWorkoutDTO.getDuration());
        assertEquals(expectedWorkout.getCaloriesBurned(), actualWorkoutDTO.getCaloriesBurned());
        assertEquals(expectedWorkout.getUserId(), actualWorkoutDTO.getUserId());
    }

    @Test
    void testGetAllWorkouts() {
        when(workoutRepository.findAllWorkouts()).thenReturn(workouts);

        List<WorkoutDTO> actualWorkoutDTOs = workoutService.getAllWorkouts();

        assertEquals(workouts.size(), actualWorkoutDTOs.size());
        assertEquals(workouts.get(0).getType(), actualWorkoutDTOs.get(0).getType());
        assertEquals(workouts.get(0).getDuration(), actualWorkoutDTOs.get(0).getDuration());
        assertEquals(workouts.get(0).getCaloriesBurned(), actualWorkoutDTOs.get(0).getCaloriesBurned());
        assertEquals(workouts.get(0).getUserId(), actualWorkoutDTOs.get(0).getUserId());

    }

    @Test
    void testUpdateWorkout() {
        WorkoutDTO updatedWorkoutDTO = workoutDTOs.get(1);
        Workout existingWorkout = new Workout(2, updatedWorkoutDTO.getType(),updatedWorkoutDTO.getDuration(), updatedWorkoutDTO.getCaloriesBurned(),
                updatedWorkoutDTO.getUserId());

        when(workoutRepository.findWorkoutById(2)).thenReturn(existingWorkout);
        doNothing().when(workoutRepository).update(existingWorkout);

        workoutService.updateWorkout(2, updatedWorkoutDTO);

        verify(workoutRepository, times(1)).update(existingWorkout);
    }

    @Test
    void testDeleteWorkout() {
        WorkoutDTO workoutDTO = workoutDTOs.get(0);
        Workout existingWorkout = new Workout(1, workoutDTO.getType(), workoutDTO.getDuration(), workoutDTO.getCaloriesBurned(), workoutDTO.getUserId());

        when(workoutRepository.findWorkoutById(1)).thenReturn(existingWorkout);
        doNothing().when(workoutRepository).delete(1); // Мокаем удаление

        workoutService.deleteWorkout(1);

        verify(workoutRepository, times(1)).delete(1);
    }

    @ParameterizedTest
    @CsvSource({
            "1, Running, 30, 360",
            "2, Cycling, 45, 315"
    })
    void testGetWorkoutsByUserId(int userId, String type, int duration, int caloriesBurned) {
        Workout workout = new Workout(0, type, duration, caloriesBurned, userId);
        List<Workout> userWorkouts = Arrays.asList(workout);

        when(workoutRepository.findWorkoutsByUserId(userId)).thenReturn(userWorkouts);

        List<WorkoutDTO> actualWorkoutsDTO = workoutService.getWorkoutsByUserId(userId);

        assertNotNull(actualWorkoutsDTO);
        assertEquals(userWorkouts.size(), actualWorkoutsDTO.size());
        assertEquals(type, actualWorkoutsDTO.get(0).getType());
        assertEquals(caloriesBurned, actualWorkoutsDTO.get(0).getCaloriesBurned());
    }
}