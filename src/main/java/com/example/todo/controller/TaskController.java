package com.example.todo.controller;

import com.example.todo.entity.Task;
import com.example.todo.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<TaskView> tasks = taskService.getAllTasks()
                .stream()
                .map(t -> new TaskView(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.isCompleted(),
                        t.getCreatedAt().toString()))
                .toList();
        model.addAttribute("tasks", tasks);
        return "index";
    }

    @PostMapping("/tasks")
    public String create(@RequestParam("title") String title,
                         @RequestParam(value = "description", required = false) String description,
                         Model model) {
        try {
            taskService.createTask(title, description);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tasks", taskService.getAllTasks()
                    .stream()
                    .map(t -> new TaskView(t.getId(), t.getTitle(), t.getDescription(),
                            t.isCompleted(), t.getCreatedAt().toString()))
                    .toList());
            return "index";
        }
    }

    @PostMapping("/tasks/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        taskService.toggleTask(id);
        return "redirect:/";
    }

    @PostMapping("/tasks/{id}/delete")
    public String delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/";
    }

    public record TaskView(Long id, String title, String description, boolean completed, String createdAt) {
    }
}
