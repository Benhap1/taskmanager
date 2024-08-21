import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
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
        User author = new User();
        when(taskRepository.save(task)).thenReturn(task);

        Task createdTask = taskService.createTask(task, author);

        assertNotNull(createdTask);
        assertEquals(author, createdTask.getAuthor());
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
        User author = new User();
        task.setAuthor(author);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Optional<Task> updatedTask = taskService.updateTask(id, task, author);

        assertTrue(updatedTask.isPresent());
        assertEquals(author, updatedTask.get().getAuthor());
        verify(taskRepository).save(task);
    }

    @Test
    public void updateTask_NotFound() {
        Long id = 1L;
        Task task = new Task();
        User currentUser = new User();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Task> updatedTask = taskService.updateTask(id, task, currentUser);

        assertFalse(updatedTask.isPresent());
    }

    @Test
    public void deleteTask_Success() {
        Long id = 1L;
        Task task = new Task();
        User author = new User();
        task.setAuthor(author);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        boolean result = taskService.deleteTask(id, author);

        assertTrue(result);
        verify(taskRepository).deleteById(id);
    }

    @Test
    public void deleteTask_NotFound() {
        Long id = 1L;
        User currentUser = new User();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = taskService.deleteTask(id, currentUser);

        assertFalse(result);
        verify(taskRepository, never()).deleteById(id);
    }

    @Test
    public void updateTaskStatus_Success() {
        Long id = 1L;
        Status status = Status.COMPLETED;
        Task task = new Task();
        User assignee = new User();
        task.setAssignee(assignee);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.updateTaskStatus(id, assignee, status);

        assertNotNull(updatedTask);
        assertEquals(status, updatedTask.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    public void updateTaskStatus_NotFound() {
        Long id = 1L;
        Status status = Status.COMPLETED;
        User currentUser = new User();

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTaskStatus(id, currentUser, status));
        verify(taskRepository, never()).save(any(Task.class));
    }
}
