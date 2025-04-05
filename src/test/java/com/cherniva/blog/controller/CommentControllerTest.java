package com.cherniva.blog.controller;

import com.cherniva.blog.model.Comment;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.service.CommentService;
import com.cherniva.blog.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;
    private Post testPost;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setText("Test Content");

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setPostId(1L);
        testComment.setComment("Test Comment");
    }

    @Test
    void addComment_WhenPostExists_ShouldAddComment() throws Exception {
        // Arrange
        when(postService.findById(1L)).thenReturn(Optional.of(testPost));
        when(commentService.save(any(Comment.class))).thenReturn(testComment);

        // Act & Assert
        mockMvc.perform(post("/posts/1/comments")
                .param("text", "New Comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        // Verify
        verify(postService, times(1)).findById(1L);
        verify(commentService, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_WhenPostDoesNotExist_ShouldRedirectToPosts() throws Exception {
        // Arrange
        when(postService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/posts/999/comments")
                .param("text", "New Comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        // Verify
        verify(postService, times(1)).findById(999L);
        verify(commentService, never()).save(any(Comment.class));
    }

    @Test
    void editComment_WhenCommentExists_ShouldUpdateComment() throws Exception {
        // Arrange
        when(commentService.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentService.save(any(Comment.class))).thenReturn(testComment);

        // Act & Assert
        mockMvc.perform(post("/posts/1/comments/1")
                .param("text", "Updated Comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        // Verify
        verify(commentService, times(1)).findById(1L);
        verify(commentService, times(1)).save(any(Comment.class));
    }

    @Test
    void editComment_WhenCommentDoesNotExist_ShouldRedirectToPost() throws Exception {
        // Arrange
        when(commentService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/posts/1/comments/999")
                .param("text", "Updated Comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        // Verify
        verify(commentService, times(1)).findById(999L);
        verify(commentService, never()).save(any(Comment.class));
    }

    @Test
    void deleteComment_ShouldDeleteComment() throws Exception {
        // Arrange
        doNothing().when(commentService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(post("/posts/1/comments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        // Verify
        verify(commentService, times(1)).deleteById(1L);
    }
}