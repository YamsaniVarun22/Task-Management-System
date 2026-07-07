package com.taskmanagementsystem.mapper;

import com.taskmanagementsystem.dto.UserDto;
import com.taskmanagementsystem.model.User;

import java.util.List;

public class UserMapper {

    public static UserDto toDTO(User user){

        if(user == null){
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static List<UserDto> toDTOList(List<User> users){

        return users.stream()
                .map(UserMapper::toDTO)
                .toList();
    }
}