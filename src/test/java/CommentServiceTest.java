import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.service.CommentService;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createComment_Success() {
        Comment comment = new Comment();
        User user = new User();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail("user@example.com")).thenReturn(new ResponseEntity<>(user, HttpStatus.OK));
        when(commentRepository.save(comment)).thenReturn(comment);

        ResponseEntity<?> response = commentService.createComment(comment, bindingResult, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(comment, response.getBody());
        verify(commentRepository).save(comment);
    }

    @Test
    public void createComment_BindingResultErrors() {
        Comment comment = new Comment();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("field", "error message")));

        ResponseEntity<?> response = commentService.createComment(comment, bindingResult, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error message", response.getBody());
    }

    @Test
    public void getCommentsByTask_Success() {
        Long taskId = 1L;
        Task task = new Task();
        Page<Comment> comments = mock(Page.class);
        when(taskService.getTaskById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.findByTask(task, Pageable.unpaged())).thenReturn(comments);

        Optional<Page<Comment>> foundComments = commentService.getCommentsByTask(taskId, Pageable.unpaged());

        assertTrue(foundComments.isPresent());
        assertEquals(comments, foundComments.get());
    }

    @Test
    public void getCommentsByTask_NotFound() {
        Long taskId = 1L;
        when(taskService.getTaskById(taskId)).thenReturn(Optional.empty());

        Optional<Page<Comment>> foundComments = commentService.getCommentsByTask(taskId, Pageable.unpaged());

        assertFalse(foundComments.isPresent());
    }

    @Test
    public void getCommentById_Success() {
        Long id = 1L;
        Comment comment = new Comment();
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        Optional<Comment> foundComment = commentService.getCommentById(id);

        assertTrue(foundComment.isPresent());
        assertEquals(comment, foundComment.get());
    }

    @Test
    public void getCommentById_NotFound() {
        Long id = 1L;
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Comment> foundComment = commentService.getCommentById(id);

        assertFalse(foundComment.isPresent());
    }
    @Test
    public void updateComment_BindingResultErrors() {
        Long id = 1L;
        Comment newComment = new Comment();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("field", "error message")));

        ResponseEntity<?> response = commentService.updateComment(id, newComment, bindingResult, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error message", response.getBody());
    }


    @Test
    public void updateComment_NotFound() {
        Long id = 1L;
        Comment newComment = new Comment();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.getUserByEmail("user@example.com")).thenReturn(new ResponseEntity<>(new User(), HttpStatus.OK));
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<?> response = commentService.updateComment(id, newComment, bindingResult, userDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteComment_Success() {
        Long id = 1L;
        User currentUser = new User();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(currentUser));
        Comment existingComment = new Comment();
        existingComment.setAuthor(currentUser);
        when(commentRepository.findById(id)).thenReturn(Optional.of(existingComment));

        ResponseEntity<Void> response = commentService.deleteComment(id, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(commentRepository).deleteById(id);
    }


    @Test
    public void deleteComment_NotFound() {
        Long id = 1L;
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(new User()));
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = commentService.deleteComment(id, userDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(commentRepository, never()).deleteById(id);
    }
}
