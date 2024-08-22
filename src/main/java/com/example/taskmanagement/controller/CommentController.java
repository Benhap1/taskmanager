package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody Comment comment, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.createComment(comment, bindingResult, userDetails);
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
    public ResponseEntity<?> updateComment(@PathVariable Long id, @Valid @RequestBody Comment newComment, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.updateComment(id, newComment, bindingResult, userDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.deleteComment(id, userDetails);
    }
}

