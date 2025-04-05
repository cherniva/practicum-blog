package com.cherniva.blog.service;

import com.cherniva.blog.model.Comment;
import com.cherniva.blog.repo.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment testComment;
    private List<Comment> testComments;

    @BeforeEach
    void setUp() {
        testComment = new Comment();
        testComment.setId(1L);
        testComment.setPostId(1L);
        testComment.setComment("Test comment");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setPostId(1L);
        comment2.setComment("Another comment");

        testComments = Arrays.asList(testComment, comment2);
    }

    @Test
    void save_ShouldReturnSavedComment() {
        // Arrange
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // Act
        Comment savedComment = commentService.save(testComment);

        // Assert
        assertNotNull(savedComment);
        assertEquals(testComment.getId(), savedComment.getId());
        assertEquals(testComment.getPostId(), savedComment.getPostId());
        assertEquals(testComment.getComment(), savedComment.getComment());
        verify(commentRepository, times(1)).save(testComment);
    }

    @Test
    void findById_WhenCommentExists_ShouldReturnComment() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        // Act
        Optional<Comment> foundComment = commentService.findById(1L);

        // Assert
        assertTrue(foundComment.isPresent());
        assertEquals(testComment.getId(), foundComment.get().getId());
        assertEquals(testComment.getPostId(), foundComment.get().getPostId());
        assertEquals(testComment.getComment(), foundComment.get().getComment());
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenCommentDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Comment> foundComment = commentService.findById(999L);

        // Assert
        assertTrue(foundComment.isEmpty());
        verify(commentRepository, times(1)).findById(999L);
    }

    @Test
    void findAll_ShouldReturnAllComments() {
        // Arrange
        when(commentRepository.findAll()).thenReturn(testComments);

        // Act
        List<Comment> foundComments = commentService.findAll();

        // Assert
        assertNotNull(foundComments);
        assertEquals(2, foundComments.size());
        verify(commentRepository, times(1)).findAll();
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        // Arrange
        Long commentId = 1L;
        doNothing().when(commentRepository).deleteById(commentId);

        // Act
        commentService.deleteById(commentId);

        // Assert
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void findByPostId_ShouldReturnCommentsForPost() {
        // Arrange
        Long postId = 1L;
        when(commentRepository.findByPostId(postId)).thenReturn(testComments);

        // Act
        List<Comment> foundComments = commentService.findByPostId(postId);

        // Assert
        assertNotNull(foundComments);
        assertEquals(2, foundComments.size());
        verify(commentRepository, times(1)).findByPostId(postId);
    }
}