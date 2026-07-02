package com.example.todo.service;

import com.example.todo.entity.Task;
import com.example.todo.repository.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Cacheable("tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .toList();
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTask(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        Task task = new Task();
        task.setTitle(title.trim());
        task.setDescription(description != null && !description.isBlank() ? description.trim() : null);
        task.setCreatedAt(OffsetDateTime.now());
        return taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task toggleTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        task.setCompleted(!task.isCompleted());
        return taskRepository.save(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
