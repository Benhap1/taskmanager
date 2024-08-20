package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTask(Task task, Pageable pageable);
}
