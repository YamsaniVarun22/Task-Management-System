package com.taskmanagementsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmanagementsystem.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull(message = "UserName must be provided")
    private String userName;

    @Column
    @NotNull(message = "Password must be provided")
    @Size(min = 8, message = "password must be minimum 8 characters")
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "User role must be provided")
    private Role role;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();
}
