package com.cherniva.blog.controller;

import com.cherniva.blog.model.Image;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.repo.ImageRepository;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageController imageController;

    private MockMvc mockMvc;
    private Image testImage;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();

        testImage = new Image();
        testImage.setId(1L);
        testImage.setImage(new byte[] {1, 2, 3, 4, 5});
    }

    @Test
    void getImage_WhenPostExistsWithImage_ShouldReturnImage() throws Exception {
        // Arrange
        when(imageRepository.findByPostId(1L)).thenReturn(Optional.of(testImage));

        // Act & Assert
        mockMvc.perform(get("/images/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    void getImage_WhenPostDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(imageRepository.findByPostId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/images/999"))
                .andExpect(status().isNotFound());
    }
}