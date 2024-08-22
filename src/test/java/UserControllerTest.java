import com.example.taskmanagement.controller.UserController;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import java.util.Collections;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Настройка объекта User
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.ASSIGNEE);

        when(bindingResult.hasErrors()).thenReturn(false);

        when(userService.registerUser(any(User.class), any(BindingResult.class)))
                .thenAnswer(invocation -> {
                    User inputUser = invocation.getArgument(0);
                    // Создаем User с ID и с закодированным паролем
                    User createdUser = new User();
                    createdUser.setId(1L); // Пример ID
                    createdUser.setEmail(inputUser.getEmail());
                    createdUser.setPassword("encodedPassword"); // Имитация закодированного пароля
                    createdUser.setRole(inputUser.getRole());
                    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
                });

        ResponseEntity<?> response = userController.registerUser(user, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        User responseBody = (User) response.getBody();
        assertEquals(user.getEmail(), responseBody.getEmail());
        assertEquals(user.getRole(), responseBody.getRole());
        assertEquals(1L, responseBody.getId());
        assertEquals("encodedPassword", responseBody.getPassword());
    }

    @Test
    void testGetUserById_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userService.getUserById(1L)).thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getUserById(1L)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUserByEmail_Success() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userService.getUserByEmail("test@example.com")).thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        ResponseEntity<User> response = userController.getUserByEmail("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testUpdateUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("updated@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.updateUser(anyLong(), any(User.class), any(BindingResult.class)))
                .thenAnswer(invocation -> {
                    User updatedUser = invocation.getArgument(1); // Получаем User из аргументов
                    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
                });

        ResponseEntity<?> response = userController.updateUser(1L, user, bindingResult);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testUpdateUser_BindingErrors() {
        User user = new User();
        ObjectError error = new ObjectError("user", "Validation error");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(error));
        when(userService.updateUser(anyLong(), any(User.class), any(BindingResult.class)))
                .thenAnswer(invocation -> {
                    BindingResult result = invocation.getArgument(2);
                    if (result.hasErrors()) {
                        String errors = result.getAllErrors().stream()
                                .map(ObjectError::getDefaultMessage)
                                .collect(Collectors.joining(", "));
                        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
                    }
                    return new ResponseEntity<>(HttpStatus.OK);
                });
        ResponseEntity<?> response = userController.updateUser(1L, user, bindingResult);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error", response.getBody());
    }

    @Test
    void testDeleteUser_Success() {
        when(userService.deleteUser(1L)).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userService.deleteUser(1L)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
