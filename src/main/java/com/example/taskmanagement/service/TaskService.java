package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task createTask(Task task, User author) {
        task.setAuthor(author);
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public Page<Task> getTasksByAuthor(User author, Pageable pageable) {
        return taskRepository.findByAuthor(author, pageable);
    }

    public Page<Task> getTasksByAssignee(User assignee, Pageable pageable) {
        return taskRepository.findByAssignee(assignee, pageable);
    }

    public Optional<Task> updateTask(Long id, Task task, User currentUser) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent() && (existingTask.get().getAuthor().equals(currentUser))) {
            task.setId(id);
            task.setAuthor(currentUser);
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
    }

    public boolean deleteTask(Long id, User currentUser) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent() && task.get().getAuthor().equals(currentUser)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Task updateTaskStatus(Long taskId, User currentUser, Status status) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            if (task.getAssignee() != null && task.getAssignee().equals(currentUser)) {
                task.setStatus(status);
                return taskRepository.save(task);
            } else {
                throw new AccessDeniedException("User is not the assignee of this task");
            }
        }
        throw new ResourceNotFoundException("Task not found with id " + taskId);
    }
}
