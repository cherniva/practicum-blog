package com.cherniva.blog.repo;

import com.cherniva.blog.model.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ImageRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void save_ShouldSaveImage() {
        // Arrange
        Image image = new Image();
        image.setImage(new byte[]{1, 2, 3, 4, 5});

        // Act
        Image savedImage = imageRepository.save(image);

        // Assert
        assertNotNull(savedImage.getId());
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, savedImage.getImage());

        // Verify in database
        Image dbImage = jdbcTemplate.queryForObject(
                "SELECT * FROM images WHERE id = ?",
                (rs, rowNum) -> {
                    Image img = new Image();
                    img.setId(rs.getLong("id"));
                    img.setImage(rs.getBytes("image"));
                    return img;
                },
                savedImage.getId()
        );
        assertNotNull(dbImage);
        assertArrayEquals(savedImage.getImage(), dbImage.getImage());
    }

    @Test
    void findById_WhenImageExists_ShouldReturnImage() {
        // Arrange
        jdbcTemplate.update(
                "INSERT INTO images (image) VALUES (?)",
                new byte[]{1, 2, 3, 4, 5}
        );
        Long imageId = jdbcTemplate.queryForObject(
                "SELECT id FROM images WHERE image = ?",
                Long.class,
                new byte[]{1, 2, 3, 4, 5}
        );

        // Act
        Optional<Image> foundImage = imageRepository.findById(imageId);

        // Assert
        assertTrue(foundImage.isPresent());
        assertEquals(imageId, foundImage.get().getId());
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, foundImage.get().getImage());
    }

    @Test
    void findById_WhenImageDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Image> foundImage = imageRepository.findById(999L);

        // Assert
        assertFalse(foundImage.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllImages() {
        // Arrange
        jdbcTemplate.update(
                "INSERT INTO images (image) VALUES (?)",
                new byte[]{1, 2, 3}
        );
        jdbcTemplate.update(
                "INSERT INTO images (image) VALUES (?)",
                new byte[]{4, 5, 6}
        );

        // Act
        List<Image> images = imageRepository.findAll();

        // Assert
        assertEquals(2, images.size());
    }

    @Test
    void deleteById_ShouldDeleteImage() {
        // Arrange
        jdbcTemplate.update(
                "INSERT INTO images (image) VALUES (?)",
                new byte[]{1, 2, 3, 4, 5}
        );
        Long imageId = jdbcTemplate.queryForObject(
                "SELECT id FROM images WHERE image = ?",
                Long.class,
                new byte[]{1, 2, 3, 4, 5}
        );

        // Act
        imageRepository.deleteById(imageId);

        // Assert
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM images WHERE id = ?",
                Integer.class,
                imageId
        );
        assertEquals(0, count);
    }

    @Test
    void findByPostId_WhenImageExists_ShouldReturnImage() {
        // Arrange
        // First create a post
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Test Post", "Test Content", 0
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE title = ?",
                Long.class,
                "Test Post"
        );

        // Then create an image and link it to the post
        jdbcTemplate.update(
                "INSERT INTO images (image) VALUES (?)",
                new byte[]{1, 2, 3, 4, 5}
        );
        Long imageId = jdbcTemplate.queryForObject(
                "SELECT id FROM images WHERE image = ?",
                Long.class,
                new byte[]{1, 2, 3, 4, 5}
        );

        // Update the post to reference the image
        jdbcTemplate.update(
                "UPDATE posts SET image_id = ? WHERE id = ?",
                imageId, postId
        );

        // Act
        Optional<Image> foundImage = imageRepository.findByPostId(postId);

        // Assert
        assertTrue(foundImage.isPresent());
        assertEquals(imageId, foundImage.get().getId());
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, foundImage.get().getImage());
    }

    @Test
    void findByPostId_WhenImageDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        // Create a post without an image
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Test Post", "Test Content", 0
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE title = ?",
                Long.class,
                "Test Post"
        );

        // Act
        Optional<Image> foundImage = imageRepository.findByPostId(postId);

        // Assert
        assertFalse(foundImage.isPresent());
    }
}