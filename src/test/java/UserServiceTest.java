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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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

        User registeredUser = userService.registerUser(user);

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

        Optional<String> jwt = userService.loginUser(email, password);

        assertTrue(jwt.isPresent());
        assertEquals("jwtToken", jwt.get());
    }

    @Test
    public void loginUser_Failure() {
        String email = "email@example.com";
        String password = "wrongPassword";
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Authentication failed"));

        Optional<String> jwt = userService.loginUser(email, password);

        assertFalse(jwt.isPresent());
    }

    @Test
    public void getUserById_Success() {
        Long id = 1L;
        User user = new User(id, "email@example.com", "password", Role.ASSIGNEE);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(id);

        assertTrue(foundUser.isPresent());
        assertEquals(id, foundUser.get().getId());
    }

    @Test
    public void updateUser_Success() {
        Long id = 1L;
        User user = new User(id, "email@example.com", "password", Role.ASSIGNEE);
        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        Optional<User> updatedUser = userService.updateUser(id, user);

        assertTrue(updatedUser.isPresent());
        assertEquals(id, updatedUser.get().getId());
    }

    @Test
    public void deleteUser_Success() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        boolean result = userService.deleteUser(id);

        assertTrue(result);
        verify(userRepository).deleteById(id);
    }

    @Test
    public void deleteUser_Failure() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(false);

        boolean result = userService.deleteUser(id);

        assertFalse(result);
        verify(userRepository, never()).deleteById(id);
    }
}
