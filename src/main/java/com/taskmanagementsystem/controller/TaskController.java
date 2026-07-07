package com.taskmanagementsystem.controller;

import com.taskmanagementsystem.dto.TaskDto;
import com.taskmanagementsystem.model.Task;
import com.taskmanagementsystem.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> saveTaskDetails(@Valid @RequestBody Task task){
        return ResponseEntity.ok(taskService.saveTask(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id){
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable long id, @Valid @RequestBody Task task){
        return ResponseEntity.ok(taskService.updateTaskById(id,task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTaskById(@PathVariable long id){
        String message = taskService.deleteTaskById(id);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDto> markAsComplete(@PathVariable long id){
        return ResponseEntity.ok(taskService.markAsCompleted(id));
    }

}
