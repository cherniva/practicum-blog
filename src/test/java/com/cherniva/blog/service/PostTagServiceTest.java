package com.cherniva.blog.service;

import com.cherniva.blog.repo.PostTagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostTagServiceTest {

    @Mock
    private PostTagRepository postTagRepository;

    @InjectMocks
    private PostTagService postTagService;

    @Test
    void addTagToPost_ShouldCallRepository() {
        // Arrange
        Long postId = 1L;
        Long tagId = 1L;
        doNothing().when(postTagRepository).addTagToPost(postId, tagId);

        // Act
        postTagService.addTagToPost(postId, tagId);

        // Assert
        verify(postTagRepository, times(1)).addTagToPost(postId, tagId);
    }

    @Test
    void removeTagFromPost_ShouldCallRepository() {
        // Arrange
        Long postId = 1L;
        Long tagId = 1L;
        doNothing().when(postTagRepository).removeTagFromPost(postId, tagId);

        // Act
        postTagService.removeTagFromPost(postId, tagId);

        // Assert
        verify(postTagRepository, times(1)).removeTagFromPost(postId, tagId);
    }

    @Test
    void findTagIdsByPostId_ShouldReturnTagIds() {
        // Arrange
        Long postId = 1L;
        List<Long> expectedTagIds = Arrays.asList(1L, 2L);
        when(postTagRepository.findTagIdsByPostId(postId)).thenReturn(expectedTagIds);

        // Act
        List<Long> foundTagIds = postTagService.findTagIdsByPostId(postId);

        // Assert
        assertNotNull(foundTagIds);
        assertEquals(2, foundTagIds.size());
        assertEquals(expectedTagIds, foundTagIds);
        verify(postTagRepository, times(1)).findTagIdsByPostId(postId);
    }

    @Test
    void findPostIdsByTagId_ShouldReturnPostIds() {
        // Arrange
        Long tagId = 1L;
        List<Long> expectedPostIds = Arrays.asList(1L, 2L);
        when(postTagRepository.findPostIdsByTagId(tagId)).thenReturn(expectedPostIds);

        // Act
        List<Long> foundPostIds = postTagService.findPostIdsByTagId(tagId);

        // Assert
        assertNotNull(foundPostIds);
        assertEquals(2, foundPostIds.size());
        assertEquals(expectedPostIds, foundPostIds);
        verify(postTagRepository, times(1)).findPostIdsByTagId(tagId);
    }
}