import com.example.taskmanagement.controller.CommentController;
import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    public void getCommentById_Success() throws Exception {
        Long id = 1L;
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText("Sample Comment");
        when(commentService.getCommentById(id)).thenReturn(Optional.of(comment));

        mockMvc.perform(get("/comments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value("Sample Comment"));
    }

    @Test
    public void getCommentById_NotFound() throws Exception {
        Long id = 1L;
        when(commentService.getCommentById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/comments/{id}", id))
                .andExpect(status().isNotFound());
    }
}


