import com.example.taskmanagement.controller.TaskController;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.model.Status;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void getTaskById_NotFound() throws Exception {
        Long id = 1L;
        when(taskService.getTaskById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isNotFound());
    }


    @Test
    public void deleteTask_Success() throws Exception {
        Long id = 1L;
        when(taskService.deleteTask(id)).thenReturn(true);

        mockMvc.perform(delete("/tasks/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteTask_NotFound() throws Exception {
        Long id = 1L;
        when(taskService.deleteTask(id)).thenReturn(false);

        mockMvc.perform(delete("/tasks/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTaskStatus_Success() throws Exception {
        Long id = 1L;
        Status status = Status.COMPLETED;
        Task task = new Task();
        when(taskService.updateTaskStatus(id, status)).thenReturn(task);

        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/{id}/status", id)
                        .param("status", status.name()))
                .andExpect(status().isOk());
    }

    @Test
    public void updateTaskStatus_NotFound() throws Exception {
        Long id = 1L;
        Status status = Status.COMPLETED;
        when(taskService.updateTaskStatus(id, status)).thenThrow(new ResourceNotFoundException("Task not found with id " + id));

        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/{id}/status", id)
                        .param("status", status.name()))
                .andExpect(status().isNotFound());
    }
}

