package com.cherniva.blog.repo;

import com.cherniva.blog.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void save_ShouldSaveComment() {
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

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setComment("Test Comment");

        // Act
        Comment savedComment = commentRepository.save(comment);

        // Assert
        assertNotNull(savedComment);
        assertNotNull(savedComment.getId());
        assertEquals(postId, savedComment.getPostId());
        assertEquals("Test Comment", savedComment.getComment());
    }

    @Test
    void findById_WhenCommentExists_ShouldReturnComment() {
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

        jdbcTemplate.update(
                "INSERT INTO comments (post_id, comment) VALUES (?, ?)",
                postId, "Test Comment"
        );
        Long commentId = jdbcTemplate.queryForObject(
                "SELECT id FROM comments WHERE comment = ?",
                Long.class,
                "Test Comment"
        );

        // Act
        Optional<Comment> foundComment = commentRepository.findById(commentId);

        // Assert
        assertTrue(foundComment.isPresent());
        assertEquals(commentId, foundComment.get().getId());
        assertEquals(postId, foundComment.get().getPostId());
        assertEquals("Test Comment", foundComment.get().getComment());
    }

    @Test
    void findById_WhenCommentDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Comment> foundComment = commentRepository.findById(999L);

        // Assert
        assertFalse(foundComment.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllComments() {
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

        jdbcTemplate.update(
                "INSERT INTO comments (post_id, comment) VALUES (?, ?)",
                postId, "First Comment"
        );
        jdbcTemplate.update(
                "INSERT INTO comments (post_id, comment) VALUES (?, ?)",
                postId, "Second Comment"
        );

        // Act
        List<Comment> comments = commentRepository.findAll();

        // Assert
        assertEquals(2, comments.size());
    }

    @Test
    void deleteById_ShouldDeleteComment() {
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

        jdbcTemplate.update(
                "INSERT INTO comments (post_id, comment) VALUES (?, ?)",
                postId, "Test Comment"
        );
        Long commentId = jdbcTemplate.queryForObject(
                "SELECT id FROM comments WHERE comment = ?",
                Long.class,
                "Test Comment"
        );

        // Act
        commentRepository.deleteById(commentId);

        // Assert
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comments WHERE id = ?",
                Integer.class,
                commentId
        );
        assertEquals(0, count);
    }

    @Test
    void findByPostId_ShouldReturnCommentsForPost() {
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

        jdbcTemplate.update(
                "INSERT INTO comments (post_id, comment) VALUES (?, ?)",
                postId, "First Comment"
        );
        jdbcTemplate.update(
                "INSERT INTO comments (post_id, comment) VALUES (?, ?)",
                postId, "Second Comment"
        );

        // Act
        List<Comment> comments = commentRepository.findByPostId(postId);

        // Assert
        assertEquals(2, comments.size());
        assertTrue(comments.stream().anyMatch(c -> c.getComment().equals("First Comment")));
        assertTrue(comments.stream().anyMatch(c -> c.getComment().equals("Second Comment")));
    }
} 