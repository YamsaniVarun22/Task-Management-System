package com.taskmanagementsystem.repository;

import com.taskmanagementsystem.model.Task;
import com.taskmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    public List<Task> findAllByUser(User user);
}
