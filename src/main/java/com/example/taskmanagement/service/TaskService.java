package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
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
import java.security.Principal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public ResponseEntity<?> createTask(Task task, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<User> userResponse = userService.getUserByEmail(principal.getName());
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new RuntimeException("User not found");
        }
        User author = userResponse.getBody();

        task.setAuthor(author);
        Task createdTask = taskRepository.save(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }
    public ResponseEntity<?> updateTask(Long id, Task task, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<User> userResponse = userService.getUserByEmail(principal.getName());
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new RuntimeException("User not found");
        }
        User currentUser = userResponse.getBody();

        Optional<Task> existingTaskOpt = taskRepository.findById(id);
        if (existingTaskOpt.isPresent() && existingTaskOpt.get().getAuthor().equals(currentUser)) {
            task.setId(id);
            task.setAuthor(currentUser);
            Task updatedTask = taskRepository.save(task);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> deleteTask(Long id, Principal principal) {
        ResponseEntity<User> userResponse = userService.getUserByEmail(principal.getName());
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User currentUser = userResponse.getBody();

        Optional<Task> existingTaskOpt = taskRepository.findById(id);
        if (existingTaskOpt.isPresent() && existingTaskOpt.get().getAuthor().equals(currentUser)) {
            taskRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Task> updateTaskStatus(Long taskId, Status status, UserDetails userDetails) {
        ResponseEntity<User> userResponse = userService.getUserByEmail(userDetails.getUsername());
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new UsernameNotFoundException("User not found");
        }
        User currentUser = userResponse.getBody();

        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            if (task.getAssignee() != null && task.getAssignee().equals(currentUser)) {
                task.setStatus(status);
                Task updatedTask = taskRepository.save(task);
                return new ResponseEntity<>(updatedTask, HttpStatus.OK);
            } else {
                throw new AccessDeniedException("User is not the assignee of this task");
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    // Метод для фильтрации задач по автору
    public ResponseEntity<Page<Task>> getTasksByAuthor(String authorEmail, Pageable pageable) {
        ResponseEntity<User> userResponse = userService.getUserByEmail(authorEmail);
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User author = userResponse.getBody();
        Page<Task> tasks = taskRepository.findByAuthor(author, pageable);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Метод для фильтрации задач по исполнителю
    public ResponseEntity<Page<Task>> getTasksByAssignee(String assigneeEmail, Pageable pageable) {
        ResponseEntity<User> userResponse = userService.getUserByEmail(assigneeEmail);
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User assignee = userResponse.getBody();
        Page<Task> tasks = taskRepository.findByAssignee(assignee, pageable);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

}
