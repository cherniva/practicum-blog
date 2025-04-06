package com.cherniva.blog.repo;

import com.cherniva.blog.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PostRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void save_ShouldSavePost() {
        // Arrange
        Post post = new Post();
        post.setTitle("Test Post");
        post.setText("Test Content");
        post.setLikes(0);

        // Act
        Post savedPost = postRepository.save(post);

        // Assert
        assertNotNull(savedPost.getId());
        assertEquals("Test Post", savedPost.getTitle());
        assertEquals("Test Content", savedPost.getText());
        assertEquals(0, savedPost.getLikes());

        // Verify in database
        Post dbPost = jdbcTemplate.queryForObject(
                "SELECT * FROM posts WHERE id = ?",
                (rs, rowNum) -> {
                    Post p = new Post();
                    p.setId(rs.getLong("id"));
                    p.setTitle(rs.getString("title"));
                    p.setText(rs.getString("text"));
                    p.setLikes(rs.getInt("likes"));
                    return p;
                },
                savedPost.getId()
        );
        assertNotNull(dbPost);
        assertEquals(savedPost.getTitle(), dbPost.getTitle());
        assertEquals(savedPost.getText(), dbPost.getText());
    }

    @Test
    void findById_WhenPostExists_ShouldReturnPost() {
        // Arrange
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
        Optional<Post> foundPost = postRepository.findById(postId);

        // Assert
        assertTrue(foundPost.isPresent());
        assertEquals(postId, foundPost.get().getId());
        assertEquals("Test Post", foundPost.get().getTitle());
    }

    @Test
    void findById_WhenPostDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Post> foundPost = postRepository.findById(999L);

        // Assert
        assertFalse(foundPost.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllPosts() {
        // Arrange
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Post 1", "Content 1", 0
        );
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Post 2", "Content 2", 0
        );

        // Act
        List<Post> posts = postRepository.findAll();

        // Assert
        assertEquals(2, posts.size());
    }

    @Test
    void deleteById_ShouldDeletePost() {
        // Arrange
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
        postRepository.deleteById(postId);

        // Assert
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts WHERE id = ?",
                Integer.class,
                postId
        );
        assertEquals(0, count);
    }

    @Test
    void findByTitle_ShouldReturnMatchingPosts() {
        // Arrange
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Test Post", "Test Content", 0
        );
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Another Post", "Another Content", 0
        );

        // Act
        List<Post> posts = postRepository.findByTitle("Test Post");

        // Assert
        assertEquals(1, posts.size());
        assertEquals("Test Post", posts.get(0).getTitle());
    }

    @Test
    void findAllWithPagination_ShouldReturnPaginatedPosts() {
        // Arrange
        for (int i = 1; i <= 5; i++) {
            jdbcTemplate.update(
                    "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                    "Post " + i, "Content " + i, 0
            );
        }

        // Act
        List<Post> posts = postRepository.findAllWithPagination(0, 2, "id", "ASC");

        // Assert
        assertEquals(2, posts.size());
        assertEquals("Post 1", posts.get(0).getTitle());
        assertEquals("Post 2", posts.get(1).getTitle());
    }

    @Test
    void countTotalPosts_ShouldReturnCorrectCount() {
        // Arrange
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Post 1", "Content 1", 0
        );
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Post 2", "Content 2", 0
        );

        // Act
        long count = postRepository.countTotalPosts();

        // Assert
        assertEquals(2, count);
    }
} 