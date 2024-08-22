import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import java.security.Principal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

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
        BindingResult bindingResult = mock(BindingResult.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail("user@example.com")).thenReturn(new ResponseEntity<>(author, HttpStatus.OK));
        when(taskRepository.save(task)).thenReturn(task);

        ResponseEntity<?> response = taskService.createTask(task, bindingResult, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(author, ((Task) response.getBody()).getAuthor());
        verify(taskRepository).save(task);
    }

    @Test
    public void getTaskById_Success() {
        Long id = 1L;
        Task task = new Task();
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        Optional<Task> foundTask = taskService.getTaskById(id);

        assertTrue(foundTask.isPresent());
        assertEquals(task, foundTask.get());
    }

    @Test
    public void updateTask_Success() {
        Long id = 1L;
        Task task = new Task();
        User author = new User();
        task.setAuthor(author);

        BindingResult bindingResult = mock(BindingResult.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail("user@example.com")).thenReturn(new ResponseEntity<>(author, HttpStatus.OK));
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        ResponseEntity<?> response = taskService.updateTask(id, task, bindingResult, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(author, ((Task) response.getBody()).getAuthor());
        verify(taskRepository).save(task);
    }

    @Test
    public void updateTask_NotFound() {
        Long id = 1L;
        Task task = new Task();
        BindingResult bindingResult = mock(BindingResult.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail("user@example.com")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(RuntimeException.class, () -> {
            taskService.updateTask(id, task, bindingResult, principal);
        });

        verify(taskRepository, never()).save(task);
    }


    @Test
    public void deleteTask_Success() {
        Long id = 1L;
        Task task = new Task();
        User author = new User();
        task.setAuthor(author);

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(new ResponseEntity<>(author, HttpStatus.OK));
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        ResponseEntity<Void> response = taskService.deleteTask(id, principal);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskRepository).deleteById(id);
    }

    @Test
    public void deleteTask_NotFound() {
        Long id = 1L;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        ResponseEntity<Void> response = taskService.deleteTask(id, principal);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskRepository, never()).deleteById(id);
    }


    @Test
    public void updateTaskStatus_Success() {
        Long id = 1L;
        Status status = Status.COMPLETED;
        Task task = new Task();
        User assignee = new User();
        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(new ResponseEntity<>(assignee, HttpStatus.OK));
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        task.setAssignee(assignee);
        when(taskRepository.save(task)).thenReturn(task);

        ResponseEntity<Task> response = taskService.updateTaskStatus(id, status, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(status, ((Task) response.getBody()).getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    public void updateTaskStatus_NotFound() {
        Long id = 1L;
        Status status = Status.COMPLETED;
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(UsernameNotFoundException.class, () -> taskService.updateTaskStatus(id, status, userDetails));
        verify(taskRepository, never()).save(any(Task.class));
    }
}
