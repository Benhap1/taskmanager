import com.example.taskmanagement.controller.TaskController;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

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
        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content("{\"id\":1,\"name\":\"\"}")
                        .principal(() -> "user@example.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTaskById_Success() throws Exception {
        Long id = 1L;
        Task task = new Task();
        when(taskService.getTaskById(id)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isOk());
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
        User currentUser = new User();
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(currentUser));
        when(taskService.deleteTask(id, currentUser)).thenReturn(true);

        mockMvc.perform(delete("/tasks/{id}", id)
                        .principal(() -> "user@example.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteTask_NotFound() throws Exception {
        Long id = 1L;
        User currentUser = new User();
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(currentUser));
        when(taskService.deleteTask(id, currentUser)).thenReturn(false);

        mockMvc.perform(delete("/tasks/{id}", id)
                        .principal(() -> "user@example.com"))
                .andExpect(status().isNotFound());
    }
}
