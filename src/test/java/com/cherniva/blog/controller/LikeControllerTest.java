package com.cherniva.blog.controller;

import com.cherniva.blog.model.Like;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.service.LikeService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private MockMvc mockMvc;
    private Post testPost;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setText("Test Content");
        testPost.setLikes(5);
    }

    @Test
    void likePost_WhenPostExistsAndLikeTrue_ShouldAddLike() throws Exception {
        // Arrange
        when(postService.findById(1L)).thenReturn(Optional.of(testPost));
        when(postService.save(any(Post.class))).thenReturn(testPost);
        when(likeService.save(any(Like.class))).thenReturn(new Like());

        int likesBefore = testPost.getLikes();

        // Act & Assert
        mockMvc.perform(post("/posts/1/like")
                .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        int likesAfter = testPost.getLikes();

        // Verify
        verify(postService, times(1)).findById(1L);
        verify(likeService, times(1)).save(any(Like.class));
        verify(postService, times(1)).save(any(Post.class));
        assertEquals(1, likesAfter-likesBefore);
    }

    @Test
    void likePost_WhenPostExistsAndLikeFalse_ShouldDecreaseLikes() throws Exception {
        // Arrange
        when(postService.findById(1L)).thenReturn(Optional.of(testPost));
        when(postService.save(any(Post.class))).thenReturn(testPost);

        int likesBefore = testPost.getLikes();

        // Act & Assert
        mockMvc.perform(post("/posts/1/like")
                .param("like", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        int likesAfter = testPost.getLikes();

        // Verify
        verify(postService, times(1)).findById(1L);
        verify(likeService, never()).save(any(Like.class));
        verify(postService, times(1)).save(any(Post.class));
        assertEquals(1, likesBefore-likesAfter);
    }

    @Test
    void likePost_WhenPostDoesNotExist_ShouldRedirectToPosts() throws Exception {
        // Arrange
        when(postService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/posts/999/like")
                .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        // Verify
        verify(postService, times(1)).findById(999L);
        verify(likeService, never()).save(any(Like.class));
        verify(postService, never()).save(any(Post.class));
    }

    @Test
    void likePost_WhenLikesAreZeroAndLikeFalse_ShouldNotDecreaseBelowZero() throws Exception {
        // Arrange
        testPost.setLikes(0);
        when(postService.findById(1L)).thenReturn(Optional.of(testPost));
        when(postService.save(any(Post.class))).thenReturn(testPost);

        // Act & Assert
        mockMvc.perform(post("/posts/1/like")
                .param("like", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        // Verify
        verify(postService, times(1)).findById(1L);
        verify(likeService, never()).save(any(Like.class));
        verify(postService, times(1)).save(any(Post.class));
    }
}