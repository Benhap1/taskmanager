package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> createComment(Comment comment, BindingResult bindingResult, UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }


        ResponseEntity<User> userResponse = userService.getUserByEmail(userDetails.getUsername());
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new UsernameNotFoundException("User not found");
        }
        User currentUser = userResponse.getBody();

        comment.setAuthor(currentUser);
        Comment createdComment = commentRepository.save(comment);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    public Optional<Page<Comment>> getCommentsByTask(Long taskId, Pageable pageable) {
        Optional<Task> task = taskService.getTaskById(taskId);
        return task.map(t -> commentRepository.findByTask(t, pageable));
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }


    public ResponseEntity<?> updateComment(Long id, Comment newComment, BindingResult bindingResult, UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<User> userResponse = userService.getUserByEmail(userDetails.getUsername());
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new UsernameNotFoundException("User not found");
        }
        User currentUser = userResponse.getBody();

        Optional<Comment> existingCommentOpt = commentRepository.findById(id);
        if (existingCommentOpt.isPresent()) {
            Comment existingComment = existingCommentOpt.get();
            if (existingComment.getAuthor() != null && existingComment.getAuthor().equals(currentUser)) {
                existingComment.setText(newComment.getText());
                Comment updatedComment = commentRepository.save(existingComment);
                return new ResponseEntity<>(updatedComment, HttpStatus.OK);
            } else {
                throw new AccessDeniedException("User is not the author of this comment");
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    public ResponseEntity<Void> deleteComment(Long id, UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<Comment> existingCommentOpt = commentRepository.findById(id);
        if (existingCommentOpt.isPresent()) {
            Comment existingComment = existingCommentOpt.get();
            if (existingComment.getAuthor() != null && existingComment.getAuthor().equals(currentUser)) {
                commentRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                throw new AccessDeniedException("User is not the author of this comment");
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
