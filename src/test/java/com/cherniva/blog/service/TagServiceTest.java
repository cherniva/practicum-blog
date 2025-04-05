package com.cherniva.blog.service;

import com.cherniva.blog.model.Tag;
import com.cherniva.blog.repo.TagRepository;
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
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag testTag;
    private List<Tag> testTags;

    @BeforeEach
    void setUp() {
        testTag = new Tag();
        testTag.setId(1L);
        testTag.setTag("Java");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setTag("Spring");

        testTags = Arrays.asList(testTag, tag2);
    }

    @Test
    void save_ShouldReturnSavedTag() {
        // Arrange
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        // Act
        Tag savedTag = tagService.save(testTag);

        // Assert
        assertNotNull(savedTag);
        assertEquals(testTag.getId(), savedTag.getId());
        assertEquals(testTag.getTag(), savedTag.getTag());
        verify(tagRepository, times(1)).save(testTag);
    }

    @Test
    void findById_WhenTagExists_ShouldReturnTag() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));

        // Act
        Optional<Tag> foundTag = tagService.findById(1L);

        // Assert
        assertTrue(foundTag.isPresent());
        assertEquals(testTag.getId(), foundTag.get().getId());
        assertEquals(testTag.getTag(), foundTag.get().getTag());
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenTagDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Tag> foundTag = tagService.findById(999L);

        // Assert
        assertTrue(foundTag.isEmpty());
        verify(tagRepository, times(1)).findById(999L);
    }

    @Test
    void findAll_ShouldReturnAllTags() {
        // Arrange
        when(tagRepository.findAll()).thenReturn(testTags);

        // Act
        List<Tag> foundTags = tagService.findAll();

        // Assert
        assertNotNull(foundTags);
        assertEquals(2, foundTags.size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        // Arrange
        Long tagId = 1L;
        doNothing().when(tagRepository).deleteById(tagId);

        // Act
        tagService.deleteById(tagId);

        // Assert
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    void findByPostId_ShouldReturnTagsForPost() {
        // Arrange
        Long postId = 1L;
        when(tagRepository.findByPostId(postId)).thenReturn(testTags);

        // Act
        List<Tag> foundTags = tagService.findByPostId(postId);

        // Assert
        assertNotNull(foundTags);
        assertEquals(2, foundTags.size());
        verify(tagRepository, times(1)).findByPostId(postId);
    }

    @Test
    void findByTag_WhenTagExists_ShouldReturnTag() {
        // Arrange
        String tagName = "Java";
        when(tagRepository.findByTag(tagName)).thenReturn(Optional.of(testTag));

        // Act
        Optional<Tag> foundTag = tagService.findByTag(tagName);

        // Assert
        assertTrue(foundTag.isPresent());
        assertEquals(testTag.getId(), foundTag.get().getId());
        assertEquals(testTag.getTag(), foundTag.get().getTag());
        verify(tagRepository, times(1)).findByTag(tagName);
    }

    @Test
    void findByTag_WhenTagDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        String tagName = "NonExistent";
        when(tagRepository.findByTag(tagName)).thenReturn(Optional.empty());

        // Act
        Optional<Tag> foundTag = tagService.findByTag(tagName);

        // Assert
        assertTrue(foundTag.isEmpty());
        verify(tagRepository, times(1)).findByTag(tagName);
    }
}