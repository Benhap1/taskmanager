import com.example.taskmanagement.controller.TaskController;
import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import java.security.Principal;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    public void createTask_BadRequest() throws Exception {

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        ObjectError error = new ObjectError("task", "Validation error");
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(error));
        when(taskService.createTask(any(Task.class), any(BindingResult.class), any(Principal.class)))
                .thenAnswer(invocation -> {
                    BindingResult result = invocation.getArgument(1);
                    if (result.hasErrors()) {
                        String errors = result.getAllErrors().stream()
                                .map(ObjectError::getDefaultMessage)
                                .collect(Collectors.joining(", "));
                        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                });

        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content("{\"name\":\"\"}") // Пример JSON контента
                        .principal(() -> "user@example.com"))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void getTaskById_Success() throws Exception {
        Long id = 1L;
        Task task = new Task();
        task.setId(id);
        task.setTitle("Sample Task");
        task.setDescription("This is a sample task description.");
        task.setStatus(Status.PENDING);
        task.setPriority(Priority.HIGH);
        task.setAuthor(new User());
        task.setAssignee(new User());
        when(taskService.getTaskById(id)).thenReturn(Optional.of(task));
        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id)) // Проверяем значение id в ответе
                .andExpect(jsonPath("$.title").value("Sample Task")) // Проверяем значение title
                .andExpect(jsonPath("$.description").value("This is a sample task description.")) // Проверяем значение description
                .andExpect(jsonPath("$.status").value("PENDING")) // Проверяем значение status
                .andExpect(jsonPath("$.priority").value("HIGH")) // Проверяем значение priority
                .andExpect(jsonPath("$.author").exists()) // Проверяем, что author присутствует
                .andExpect(jsonPath("$.assignee").exists()); // Проверяем, что assignee присутствует
    }


    @Test
    public void getTaskById_NotFound() throws Exception {
        Long id = 1L;
        when(taskService.getTaskById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteTask_Success() throws Exception {
        Long id = 1L;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");
        when(taskService.deleteTask(id, principal)).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        mockMvc.perform(delete("/tasks/{id}", id)
                        .principal(principal))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteTask_NotFound() throws Exception {
        Long id = 1L;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");
        when(taskService.deleteTask(id, principal)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        mockMvc.perform(delete("/tasks/{id}", id)
                        .principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTask_NotFound() throws Exception {
        Long id = 1L;
        Task task = new Task();
        BindingResult bindingResult = mock(BindingResult.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(taskService.updateTask(eq(id), any(Task.class), any(BindingResult.class), eq(principal)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType("application/json")
                        .content("{\"name\":\"Task Name\"}") // Пример JSON контента
                        .principal(principal))
                .andExpect(status().isNotFound());
    }
}
