package com.taskmanagementsystem.dto;

import com.taskmanagementsystem.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String userName;

    private Role role;

    private LocalDateTime createdAt;
}