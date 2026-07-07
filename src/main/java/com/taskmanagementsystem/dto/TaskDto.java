package com.taskmanagementsystem.dto;

import com.taskmanagementsystem.enums.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private long id;

    private String title;

    private String description;

    private Date dueDate;

    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}