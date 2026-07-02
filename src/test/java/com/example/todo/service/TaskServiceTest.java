package com.example.todo.service;

import com.example.todo.entity.Task;
import com.example.todo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository);
    }

    @Test
    void createTask_savesWithCorrectFields() {
        taskService.createTask("Buy groceries", "Milk and eggs");

        verify(taskRepository).save(argThat(task ->
                "Buy groceries".equals(task.getTitle()) &&
                "Milk and eggs".equals(task.getDescription()) &&
                !task.isCompleted() &&
                task.getCreatedAt() != null
        ));
    }

    @Test
    void createTask_withBlankTitle_throws() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask("  ", null)
        );
        verifyNoInteractions(taskRepository);
    }

    @Test
    void deleteTask_callsRepository() {
        taskService.deleteTask(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void toggleTask_flipsCompletedFlag() {
        Task task = new Task();
        task.setTitle("Test task");
        task.setCompleted(false);
        task.setCreatedAt(OffsetDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        taskService.toggleTask(1L);

        verify(taskRepository).save(argThat(Task::isCompleted));
    }

    @Test
    void toggleTask_alreadyCompleted_setsIncomplete() {
        Task task = new Task();
        task.setTitle("Done task");
        task.setCompleted(true);
        task.setCreatedAt(OffsetDateTime.now());

        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        taskService.toggleTask(2L);

        verify(taskRepository).save(argThat(t -> !t.isCompleted()));
    }
}
