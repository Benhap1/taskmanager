import com.example.taskmanagement.controller.UserController;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.JwtService;
import com.example.taskmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void registerUser_Success() throws Exception {
        User user = new User(null, "email@example.com", "password", Role.ASSIGNEE);
        when(userService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"email@example.com\",\"password\":\"password\",\"role\":\"ASSIGNEE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("email@example.com"))
                .andExpect(jsonPath("$.password").value("password"))
                .andExpect(jsonPath("$.role").value("ASSIGNEE"));
    }

    @Test
    public void loginUser_Success() throws Exception {
        String jwt = "jwtToken";
        when(userService.loginUser(anyString(), anyString())).thenReturn(Optional.of(jwt));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"email@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(jwt));
    }

    @Test
    public void loginUser_Failure() throws Exception {
        when(userService.loginUser(anyString(), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"email@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    public void getUserById_Success() throws Exception {
        Long id = 1L;
        User user = new User(id, "email@example.com", "password", Role.ASSIGNEE);
        when(userService.getUserById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("email@example.com"));
    }

    @Test
    public void getUserById_NotFound() throws Exception {
        Long id = 1L;
        when(userService.getUserById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_Success() throws Exception {
        Long id = 1L;
        User user = new User(id, "email@example.com", "password", Role.ASSIGNEE);
        when(userService.updateUser(eq(id), any(User.class))).thenReturn(Optional.of(user));

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"email@example.com\",\"password\":\"password\",\"role\":\"ASSIGNEE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("email@example.com"));
    }

    @Test
    public void updateUser_NotFound() throws Exception {
        Long id = 1L;
        when(userService.updateUser(eq(id), any(User.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"email@example.com\",\"password\":\"password\",\"role\":\"ASSIGNEE\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser_Success() throws Exception {
        Long id = 1L;
        when(userService.deleteUser(id)).thenReturn(true);

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUser_NotFound() throws Exception {
        Long id = 1L;
        when(userService.deleteUser(id)).thenReturn(false);

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNotFound());
    }
}