import com.example.taskmanagement.controller.CommentController;
import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.CommentService;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private TaskService taskService;
    
    @Mock
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private CommentController commentController;

    private ObjectMapper objectMapper;
    private User testUser;
    private Comment testComment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(commentController)
                .build();
        objectMapper = new ObjectMapper();
        testUser = new User();
        testUser.setEmail("test@example.com");
        testComment = new Comment();
        testComment.setAuthor(testUser);
        testComment.setText("This is a comment");
    }

    @Test
    public void getCommentById_Success() throws Exception {
        Long id = 1L;
        Comment comment = new Comment();
        when(commentService.getCommentById(id)).thenReturn(Optional.of(comment));
        mockMvc.perform(get("/comments/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    public void getCommentById_NotFound() throws Exception {
        Long id = 1L;
        when(commentService.getCommentById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/comments/{id}", id))
                .andExpect(status().isNotFound());
    }
}

