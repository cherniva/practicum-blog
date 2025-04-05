package com.cherniva.blog.service;

import com.cherniva.blog.model.Like;
import com.cherniva.blog.repo.LikeRepository;
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
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    private Like testLike;
    private List<Like> testLikes;

    @BeforeEach
    void setUp() {
        testLike = new Like();
        testLike.setId(1L);
        testLike.setPostId(1L);

        Like like2 = new Like();
        like2.setId(2L);
        like2.setPostId(1L);

        testLikes = Arrays.asList(testLike, like2);
    }

    @Test
    void save_ShouldReturnSavedLike() {
        // Arrange
        when(likeRepository.save(any(Like.class))).thenReturn(testLike);

        // Act
        Like savedLike = likeService.save(testLike);

        // Assert
        assertNotNull(savedLike);
        assertEquals(testLike.getId(), savedLike.getId());
        assertEquals(testLike.getPostId(), savedLike.getPostId());
        verify(likeRepository, times(1)).save(testLike);
    }

    @Test
    void findById_WhenLikeExists_ShouldReturnLike() {
        // Arrange
        when(likeRepository.findById(1L)).thenReturn(Optional.of(testLike));

        // Act
        Optional<Like> foundLike = likeService.findById(1L);

        // Assert
        assertTrue(foundLike.isPresent());
        assertEquals(testLike.getId(), foundLike.get().getId());
        assertEquals(testLike.getPostId(), foundLike.get().getPostId());
        verify(likeRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenLikeDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(likeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Like> foundLike = likeService.findById(999L);

        // Assert
        assertTrue(foundLike.isEmpty());
        verify(likeRepository, times(1)).findById(999L);
    }

    @Test
    void findAll_ShouldReturnAllLikes() {
        // Arrange
        when(likeRepository.findAll()).thenReturn(testLikes);

        // Act
        List<Like> foundLikes = likeService.findAll();

        // Assert
        assertNotNull(foundLikes);
        assertEquals(2, foundLikes.size());
        verify(likeRepository, times(1)).findAll();
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        // Arrange
        Long likeId = 1L;
        doNothing().when(likeRepository).deleteById(likeId);

        // Act
        likeService.deleteById(likeId);

        // Assert
        verify(likeRepository, times(1)).deleteById(likeId);
    }

    @Test
    void findByPostId_ShouldReturnLikesForPost() {
        // Arrange
        Long postId = 1L;
        when(likeRepository.findByPostId(postId)).thenReturn(testLikes);

        // Act
        List<Like> foundLikes = likeService.findByPostId(postId);

        // Assert
        assertNotNull(foundLikes);
        assertEquals(2, foundLikes.size());
        verify(likeRepository, times(1)).findByPostId(postId);
    }
}