package com.taskmanagementsystem.service;

import com.taskmanagementsystem.dto.TaskDto;
import com.taskmanagementsystem.enums.Status;
import com.taskmanagementsystem.exception.TaskNotFoundException;
import com.taskmanagementsystem.exception.UnauthorizedTaskAccessException;
import com.taskmanagementsystem.exception.UserNotFoundException;
import com.taskmanagementsystem.mapper.TaskMapper;
import com.taskmanagementsystem.model.Task;
import com.taskmanagementsystem.model.User;
import com.taskmanagementsystem.repository.TaskRepository;
import com.taskmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public TaskDto saveTask(Task task){
        task.setUser(getCurrentUser());
        return TaskMapper.toDTO(taskRepository.save(task));
    }

    public TaskDto getTaskById(long id ){
        Task task = taskRepository.findById(id).
                orElseThrow(() -> new TaskNotFoundException("There is no task exist with Id : " + id));
        if (!task.getUser().getId().equals(getCurrentUser().getId())) {
            throw new UnauthorizedTaskAccessException("You cannot access this task.");
        }
        return TaskMapper.toDTO(task);
    }

    public List<TaskDto> getAllTasks(){
        return TaskMapper.toDTOList(taskRepository.findAllByUser(getCurrentUser()));
    }

    public TaskDto updateTaskById(long id, Task task){
        Task existingTask = getAuthorizedTask(id);
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus());
        existingTask.setDueDate(task.getDueDate());

        return TaskMapper.toDTO(taskRepository.save(existingTask));
    }

    public String deleteTaskById(long id){
        Task task = getAuthorizedTask(id);
        taskRepository.delete(task);
        return "Succesfully removed Task";
    }

    public TaskDto markAsCompleted(long id){
       Task existing = getAuthorizedTask(id);
        existing.setStatus(Status.COMPLETED);
        return TaskMapper.toDTO(taskRepository.save(existing));
    }

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return Optional.ofNullable(userRepository.findByUserName(username))
                .orElseThrow(() ->
                        new UserNotFoundException("Authenticated user not found."));
    }

    private Task getAuthorizedTask(long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found with id : " + id));

        if (!task.getUser().getId().equals(getCurrentUser().getId())) {
            throw new UnauthorizedTaskAccessException("You cannot access this task.");
        }

        return task;
    }
}
