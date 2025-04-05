package com.cherniva.blog.converter;

import com.cherniva.blog.dto.PostDto;
import com.cherniva.blog.model.Comment;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.model.Tag;
import com.cherniva.blog.service.CommentService;
import com.cherniva.blog.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostDtoConverterTest {

    @Mock
    private CommentService commentService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private PostDtoConverter postDtoConverter;

    private Post testPost;
    private List<Comment> testComments;
    private List<Tag> testTags;

    @BeforeEach
    void setUp() {
        // Setup test post
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setText("First paragraph.\n\nSecond paragraph.\n\nThird paragraph.");
        testPost.setLikes(5);

        // Setup test comments
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setPostId(1L);
        comment1.setComment("First comment");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setPostId(1L);
        comment2.setComment("Second comment");

        testComments = Arrays.asList(comment1, comment2);

        // Setup test tags
        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setTag("Java");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setTag("Spring");

        testTags = Arrays.asList(tag1, tag2);
    }

    @Test
    void postToPostDto_ShouldConvertPostWithAllFields() {
        // Arrange
        when(commentService.findByPostId(1L)).thenReturn(testComments);
        when(tagService.findByPostId(1L)).thenReturn(testTags);

        // Act
        PostDto postDto = postDtoConverter.postToPostDto(testPost);

        // Assert
        assertNotNull(postDto);
        assertEquals(testPost.getId(), postDto.getPostId());
        assertEquals(testPost.getTitle(), postDto.getTitle());
        assertEquals("First paragraph.", postDto.getTextPreview());
        assertEquals(3, postDto.getTextParts().size());
        assertEquals(testPost.getLikes(), postDto.getLikesCount());
        assertEquals(testComments, postDto.getComments());
        assertEquals(testTags, postDto.getTags());
    }

    @Test
    void postToPostDto_ShouldHandleNullComments() {
        // Arrange
        when(commentService.findByPostId(1L)).thenReturn(null);
        when(tagService.findByPostId(1L)).thenReturn(testTags);

        // Act
        PostDto postDto = postDtoConverter.postToPostDto(testPost);

        // Assert
        assertNotNull(postDto);
        assertNotNull(postDto.getComments());
        assertTrue(postDto.getComments().isEmpty());
    }

    @Test
    void postToPostDto_ShouldHandleNullTags() {
        // Arrange
        when(commentService.findByPostId(1L)).thenReturn(testComments);
        when(tagService.findByPostId(1L)).thenReturn(null);

        // Act
        PostDto postDto = postDtoConverter.postToPostDto(testPost);

        // Assert
        assertNotNull(postDto);
        assertNotNull(postDto.getTags());
        assertTrue(postDto.getTags().isEmpty());
    }

    @Test
    void postToPostDto_ShouldHandleSingleParagraph() {
        // Arrange
        testPost.setText("Single paragraph text");
        when(commentService.findByPostId(1L)).thenReturn(testComments);
        when(tagService.findByPostId(1L)).thenReturn(testTags);

        // Act
        PostDto postDto = postDtoConverter.postToPostDto(testPost);

        // Assert
        assertNotNull(postDto);
        assertEquals("Single paragraph text", postDto.getTextPreview());
        assertEquals(1, postDto.getTextParts().size());
        assertEquals("Single paragraph text", postDto.getTextParts().get(0));
    }
}