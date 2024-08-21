package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskService taskService;

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Optional<Page<Comment>> getCommentsByTask(Long taskId, Pageable pageable) {
        Optional<Task> task = taskService.getTaskById(taskId);
        return task.map(t -> commentRepository.findByTask(t, pageable));
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public Optional<Comment> updateComment(Long id, Comment newComment, User currentUser) {
        Optional<Comment> existingCommentOpt = commentRepository.findById(id);
        if (existingCommentOpt.isPresent()) {
            Comment existingComment = existingCommentOpt.get();
            if (existingComment.getAuthor() != null && existingComment.getAuthor().equals(currentUser)) {
                existingComment.setText(newComment.getText());
                return Optional.of(commentRepository.save(existingComment));
            } else {
                throw new AccessDeniedException("User is not the author of this comment");
            }
        }
        return Optional.empty();
    }

    public boolean deleteComment(Long id, User currentUser) {
        Optional<Comment> existingCommentOpt = commentRepository.findById(id);
        if (existingCommentOpt.isPresent()) {
            Comment existingComment = existingCommentOpt.get();
            if (existingComment.getAuthor() != null && existingComment.getAuthor().equals(currentUser)) {
                commentRepository.deleteById(id);
                return true;
            } else {
                throw new AccessDeniedException("User is not the author of this comment");
            }
        }
        return false;
    }
}


