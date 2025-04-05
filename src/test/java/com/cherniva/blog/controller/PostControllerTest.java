package com.cherniva.blog.controller;

import com.cherniva.blog.converter.PostDtoConverter;
import com.cherniva.blog.dto.PostDto;
import com.cherniva.blog.model.Image;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.model.Tag;
import com.cherniva.blog.repo.ImageRepository;
import com.cherniva.blog.service.PostService;
import com.cherniva.blog.service.PostTagService;
import com.cherniva.blog.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private PostDtoConverter postDtoConverter;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private TagService tagService;

    @Mock
    private PostTagService postTagService;

    @InjectMocks
    private PostController postController;

    private MockMvc mockMvc;
    private Post testPost;
    private PostDto testPostDto;
    private Image testImage;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setViewResolvers(viewResolver)
                .build();

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setText("Test Content");
        testPost.setImageId(1L);
        testPost.setLikes(0);

        testPostDto = new PostDto();
        testPostDto.setPostId(1L);
        testPostDto.setTitle("Test Post");
        testPostDto.setTextPreview("Test Content");
        testPostDto.setLikesCount(0);
        testPostDto.setComments(Collections.emptyList());
        testPostDto.setTags(Collections.emptyList());

        testImage = new Image();
        testImage.setId(1L);
        testImage.setImage(new byte[]{1, 2, 3, 4, 5});

        testTag = new Tag();
        testTag.setId(1L);
        testTag.setTag("test");
    }

    @Test
    void allPosts_ShouldReturnPostsView() throws Exception {
        // Arrange
        List<Post> posts = Collections.singletonList(testPost);
        when(postService.findAllWithPagination(1, 10, "id", "ASC")).thenReturn(posts);
        when(postDtoConverter.postToPostDto(any(Post.class))).thenReturn(testPostDto);
        when(postService.countTotalPosts()).thenReturn(1L);

        // Act & Assert
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("paging"));

        // Verify
        verify(postService, times(1)).findAllWithPagination(1, 10, "id", "ASC");
        verify(postDtoConverter, times(1)).postToPostDto(any(Post.class));
        verify(postService, times(1)).countTotalPosts();
    }

    @Test
    void allPosts_WithSearchTag_ShouldDetectAttribute() throws Exception {
        // Arrange
        List<Post> posts = Collections.singletonList(testPost);
        when(postService.findAllWithPagination(1, 10, "id", "ASC")).thenReturn(posts);
        when(postDtoConverter.postToPostDto(any(Post.class))).thenReturn(testPostDto);
        when(postService.countTotalPosts()).thenReturn(1L);

        // Act & Assert
        mockMvc.perform(get("/posts")
                .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("search"));

        // Verify
        verify(postService, times(1)).findAllWithPagination(1, 10, "id", "ASC");
    }

    @Test
    void addPost_ShouldCreateNewPost() throws Exception {
        // Arrange
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(imageRepository.save(any(Image.class))).thenReturn(testImage);
        when(postService.save(any(Post.class))).thenReturn(testPost);
        when(tagService.save(any(Tag.class))).thenReturn(testTag);

        // Act & Assert
        mockMvc.perform(multipart("/posts")
                .file(imageFile)
                .param("title", "New Post")
                .param("text", "New Content")
                .param("tags", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        // Verify
        verify(imageRepository, times(1)).save(any(Image.class));
        verify(postService, times(1)).save(any(Post.class));
        verify(tagService, times(1)).save(any(Tag.class));
    }

    @Test
    void addPost_WithoutImage_ShouldCreatePost() throws Exception {
        // Arrange
        when(postService.save(any(Post.class))).thenReturn(testPost);

        // Act & Assert
        mockMvc.perform(post("/posts")
                .param("title", "New Post")
                .param("text", "New Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        // Verify
        verify(postService, times(1)).save(any(Post.class));
        verify(imageRepository, never()).save(any(Image.class));
    }

    @Test
    void getPost_WhenPostExists_ShouldReturnPostView() throws Exception {
        // Arrange
        when(postService.findById(1L)).thenReturn(Optional.of(testPost));
        when(postDtoConverter.postToPostDto(testPost)).thenReturn(testPostDto);

        // Act & Assert
        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"));

        // Verify
        verify(postService, times(1)).findById(1L);
        verify(postDtoConverter, times(1)).postToPostDto(testPost);
    }

    @Test
    void getPost_WhenPostDoesNotExist_ShouldRedirectToPosts() throws Exception {
        // Arrange
        when(postService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/posts/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        // Verify
        verify(postService, times(1)).findById(999L);
    }

    @Test
    void editPost_WhenPostExists_ShouldUpdatePost() throws Exception {
        // Arrange
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(postService.findById(1L)).thenReturn(Optional.of(testPost));
        when(imageRepository.save(any(Image.class))).thenReturn(testImage);
        when(postService.save(any(Post.class))).thenReturn(testPost);
        when(tagService.save(any(Tag.class))).thenReturn(testTag);

        // Act & Assert
        mockMvc.perform(multipart("/posts/1")
                .file(imageFile)
                .param("title", "Updated Title")
                .param("text", "Updated Content")
                .param("tags", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/1"));

        // Verify
        verify(postService, times(1)).findById(1L);
        verify(imageRepository, times(1)).save(any(Image.class));
        verify(postService, times(1)).save(any(Post.class));
    }

    @Test
    void editPost_WhenPostDoesNotExist_ShouldRedirectToPosts() throws Exception {
        // Arrange
        when(postService.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/posts/999")
                .param("title", "Updated Title")
                .param("text", "Updated Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        // Verify
        verify(postService, times(1)).findById(999L);
        verify(postService, never()).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldDeletePost() throws Exception {
        // Arrange
        doNothing().when(postService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(post("/posts/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        // Verify
        verify(postService, times(1)).deleteById(1L);
    }
}