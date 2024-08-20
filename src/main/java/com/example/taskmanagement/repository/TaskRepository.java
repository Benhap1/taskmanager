package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthor(User author, Pageable pageable);

    Page<Task> findByAssignee(User assignee, Pageable pageable);

    Task save(Task task);

    void delete(Task task);
}
