package com.cherniva.blog.service;

import com.cherniva.blog.model.Post;
import com.cherniva.blog.model.Tag;
import com.cherniva.blog.repo.PostRepository;
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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private List<Post> testPosts;
    private List<Tag> testTags;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setText("Test Content");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Another Post");
        post2.setText("Another Content");

        testPosts = Arrays.asList(testPost, post2);

        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setTag("Java");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setTag("Spring");

        testTags = Arrays.asList(tag1, tag2);
    }

    @Test
    void save_ShouldReturnSavedPost() {
        // Arrange
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Post savedPost = postService.save(testPost);

        // Assert
        assertNotNull(savedPost);
        assertEquals(testPost.getId(), savedPost.getId());
        assertEquals(testPost.getTitle(), savedPost.getTitle());
        assertEquals(testPost.getText(), savedPost.getText());
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    void findById_WhenPostExists_ShouldReturnPost() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act
        Optional<Post> foundPost = postService.findById(1L);

        // Assert
        assertTrue(foundPost.isPresent());
        assertEquals(testPost.getId(), foundPost.get().getId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenPostDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Post> foundPost = postService.findById(999L);

        // Assert
        assertTrue(foundPost.isEmpty());
        verify(postRepository, times(1)).findById(999L);
    }

    @Test
    void findAll_ShouldReturnAllPosts() {
        // Arrange
        when(postRepository.findAll()).thenReturn(testPosts);

        // Act
        List<Post> foundPosts = postService.findAll();

        // Assert
        assertNotNull(foundPosts);
        assertEquals(2, foundPosts.size());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void findAllWithPagination_ShouldReturnPaginatedPosts() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 10;
        String sortBy = "id";
        String sortDirection = "ASC";
        int offset = (pageNumber - 1) * pageSize;

        when(postRepository.findAllWithPagination(offset, pageSize, sortBy, sortDirection))
                .thenReturn(testPosts);

        // Act
        List<Post> foundPosts = postService.findAllWithPagination(pageNumber, pageSize, sortBy, sortDirection);

        // Assert
        assertNotNull(foundPosts);
        assertEquals(2, foundPosts.size());
        verify(postRepository, times(1))
                .findAllWithPagination(offset, pageSize, sortBy, sortDirection);
    }

    @Test
    void countTotalPosts_ShouldReturnTotalCount() {
        // Arrange
        long expectedCount = 2L;
        when(postRepository.countTotalPosts()).thenReturn(expectedCount);

        // Act
        long actualCount = postService.countTotalPosts();

        // Assert
        assertEquals(expectedCount, actualCount);
        verify(postRepository, times(1)).countTotalPosts();
    }

    @Test
    void findByTags_ShouldReturnPostsWithGivenTags() {
        // Arrange
        List<Long> tagIds = testTags.stream()
                .map(Tag::getId)
                .toList();
        
        when(postRepository.findByTags(tagIds)).thenReturn(testPosts);

        // Act
        List<Post> foundPosts = postService.findByTags(testTags);

        // Assert
        assertNotNull(foundPosts);
        assertEquals(2, foundPosts.size());
        verify(postRepository, times(1)).findByTags(tagIds);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        // Arrange
        Long postId = 1L;
        doNothing().when(postRepository).deleteById(postId);

        // Act
        postService.deleteById(postId);

        // Assert
        verify(postRepository, times(1)).deleteById(postId);
    }
}