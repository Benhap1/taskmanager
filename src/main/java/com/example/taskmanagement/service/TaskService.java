package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Page<Task> getTasksByAuthor(User author, Pageable pageable) {
        return taskRepository.findByAuthor(author, pageable);
    }

    public Page<Task> getTasksByAssignee(User assignee, Pageable pageable) {
        return taskRepository.findByAssignee(assignee, pageable);
    }

    public Optional<Task> updateTask(Long id, Task task) {
        if (taskRepository.existsById(id)) {
            task.setId(id);
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Task updateTaskStatus(Long taskId, Status status) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setStatus(status);
            return taskRepository.save(task);
        }
        throw new ResourceNotFoundException("Task not found with id " + taskId);
    }
}
