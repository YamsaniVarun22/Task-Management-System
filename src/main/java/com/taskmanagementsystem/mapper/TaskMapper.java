package com.taskmanagementsystem.mapper;

import com.taskmanagementsystem.dto.TaskDto;
import com.taskmanagementsystem.model.Task;

import java.util.List;

public class TaskMapper {

    public static TaskDto toDTO(Task task) {

        if (task == null) {
            return null;
        }

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public static List<TaskDto> toDTOList(List<Task> tasks){

        return tasks.stream()
                .map(TaskMapper::toDTO)
                .toList();
    }
}