package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.service.CommentService;
import com.example.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody Comment comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Comment createdComment = commentService.createComment(comment);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<Comment>> getCommentsByTask(@PathVariable Long taskId, Pageable pageable) {
        Optional<Page<Comment>> comments = commentService.getCommentsByTask(taskId, pageable);
        return comments.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @Valid @RequestBody Comment newComment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Optional<Comment> updatedComment = commentService.updateComment(id, newComment);
        return updatedComment.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        if (commentService.deleteComment(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
