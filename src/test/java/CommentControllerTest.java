import com.example.taskmanagement.controller.CommentController;
import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.service.CommentService;
import com.example.taskmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();
    }



    @Test
    public void createComment_ValidationError() throws Exception {
        Comment comment = new Comment();
        String content = objectMapper.writeValueAsString(comment);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assert(responseBody.contains("Text cannot be blank"));
                    assert(responseBody.contains("Task cannot be null"));
                    assert(responseBody.contains("Author cannot be null"));
                });
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



    @Test
    public void updateComment_ValidationError() throws Exception {
        Long id = 1L;
        Comment comment = new Comment();
        String content = objectMapper.writeValueAsString(comment);

        mockMvc.perform(put("/comments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assert(responseBody.contains("Text cannot be blank"));
                    assert(responseBody.contains("Task cannot be null"));
                    assert(responseBody.contains("Author cannot be null"));
                });
    }



    @Test
    public void deleteComment_Success() throws Exception {
        Long id = 1L;
        when(commentService.deleteComment(id)).thenReturn(true);

        mockMvc.perform(delete("/comments/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteComment_NotFound() throws Exception {
        Long id = 1L;
        when(commentService.deleteComment(id)).thenReturn(false);

        mockMvc.perform(delete("/comments/{id}", id))
                .andExpect(status().isNotFound());
    }
}
