import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createTask_Success() {
        Task task = new Task();
        when(taskRepository.save(task)).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        verify(taskRepository).save(task);
    }

    @Test
    public void getTaskById_Success() {
        Long id = 1L;
        Task task = new Task();
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        Optional<Task> foundTask = taskService.getTaskById(id);

        assertTrue(foundTask.isPresent());
    }

    @Test
    public void updateTask_Success() {
        Long id = 1L;
        Task task = new Task();
        when(taskRepository.existsById(id)).thenReturn(true);
        when(taskRepository.save(task)).thenReturn(task);

        Optional<Task> updatedTask = taskService.updateTask(id, task);

        assertTrue(updatedTask.isPresent());
        verify(taskRepository).save(task);
    }

    @Test
    public void updateTask_NotFound() {
        Long id = 1L;
        Task task = new Task();
        when(taskRepository.existsById(id)).thenReturn(false);

        Optional<Task> updatedTask = taskService.updateTask(id, task);

        assertFalse(updatedTask.isPresent());
    }

    @Test
    public void deleteTask_Success() {
        Long id = 1L;
        when(taskRepository.existsById(id)).thenReturn(true);

        boolean result = taskService.deleteTask(id);

        assertTrue(result);
        verify(taskRepository).deleteById(id);
    }

    @Test
    public void deleteTask_NotFound() {
        Long id = 1L;
        when(taskRepository.existsById(id)).thenReturn(false);

        boolean result = taskService.deleteTask(id);

        assertFalse(result);
        verify(taskRepository, never()).deleteById(id);
    }

    @Test
    public void updateTaskStatus_Success() {
        Long id = 1L;
        Status status = Status.COMPLETED;
        Task task = new Task();
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.updateTaskStatus(id, status);

        assertNotNull(updatedTask);
        assertEquals(status, updatedTask.getStatus());
    }

    @Test
    public void updateTaskStatus_NotFound() {
        Long id = 1L;
        Status status = Status.COMPLETED;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTaskStatus(id, status));
    }
}
