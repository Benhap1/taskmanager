import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.service.CommentService;
import com.example.taskmanagement.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createComment_Success() {
        Comment comment = new Comment();
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment createdComment = commentService.createComment(comment);

        assertNotNull(createdComment);
        verify(commentRepository).save(comment);
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
    }

    @Test
    public void getCommentById_NotFound() {
        Long id = 1L;
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Comment> foundComment = commentService.getCommentById(id);

        assertFalse(foundComment.isPresent());
    }

    @Test
    public void updateComment_Success() {
        Long id = 1L;
        Comment existingComment = new Comment();
        Comment newComment = new Comment();
        when(commentRepository.findById(id)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(existingComment)).thenReturn(existingComment);

        Optional<Comment> updatedComment = commentService.updateComment(id, newComment);

        assertTrue(updatedComment.isPresent());
        verify(commentRepository).save(existingComment);
    }

    @Test
    public void updateComment_NotFound() {
        Long id = 1L;
        Comment newComment = new Comment();
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Comment> updatedComment = commentService.updateComment(id, newComment);

        assertFalse(updatedComment.isPresent());
    }

    @Test
    public void deleteComment_Success() {
        Long id = 1L;
        when(commentRepository.existsById(id)).thenReturn(true);

        boolean result = commentService.deleteComment(id);

        assertTrue(result);
        verify(commentRepository).deleteById(id);
    }

    @Test
    public void deleteComment_NotFound() {
        Long id = 1L;
        when(commentRepository.existsById(id)).thenReturn(false);

        boolean result = commentService.deleteComment(id);

        assertFalse(result);
        verify(commentRepository, never()).deleteById(id);
    }
}

