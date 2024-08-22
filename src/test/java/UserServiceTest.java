import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.service.JwtService;
import com.example.taskmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUser_Success() {
        User user = new User(null, "email@example.com", "password", Role.ASSIGNEE);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = userService.registerUser(user, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        User registeredUser = (User) response.getBody();
        assertNotNull(registeredUser);
        assertEquals("encodedPassword", registeredUser.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    public void loginUser_Success() {
        String email = "email@example.com";
        String password = "password";
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(userDetails)).thenReturn("jwtToken");

        ResponseEntity<?> response = userService.loginUser(email, password);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwtToken", response.getBody());
    }

    @Test
    public void loginUser_Failure() {
        String email = "email@example.com";
        String password = "wrongPassword";
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Authentication failed"));

        ResponseEntity<?> response = userService.loginUser(email, password);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }

    @Test
    public void getUserById_Success() {
        Long id = 1L;
        User user = new User(id, "email@example.com", "password", Role.ASSIGNEE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userService.getUserById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() != null && response.getBody().getId().equals(id));
    }

    @Test
    public void getUserById_NotFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userService.getUserById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void updateUser_Success() {
        Long id = 1L;
        User user = new User(id, "email@example.com", "password", Role.ASSIGNEE);
        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = userService.updateUser(id, user, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User updatedUser = (User) response.getBody();
        assertNotNull(updatedUser);
        assertEquals(id, updatedUser.getId());
    }

    @Test
    public void updateUser_NotFound() {
        Long id = 1L;
        User user = new User(id, "email@example.com", "password", Role.ASSIGNEE);
        when(userRepository.existsById(id)).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = userService.updateUser(id, user, bindingResult);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteUser_Success() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        ResponseEntity<Void> response = userService.deleteUser(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository).deleteById(id);
    }

    @Test
    public void deleteUser_NotFound() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(false);

        ResponseEntity<Void> response = userService.deleteUser(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, never()).deleteById(id);
    }
}
