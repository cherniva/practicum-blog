package com.cherniva.blog.repo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostTagRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Disabled("H2 does not have IGNORE key word")
    void addTagToPost_ShouldCreateAssociation() {
        // Arrange
        // Create a post
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Test Post", "Test Content", 0
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE title = ?",
                Long.class,
                "Test Post"
        );

        // Create a tag
        jdbcTemplate.update(
                "INSERT INTO tags (tag) VALUES (?)",
                "Java"
        );
        Long tagId = jdbcTemplate.queryForObject(
                "SELECT id FROM tags WHERE tag = ?",
                Long.class,
                "Java"
        );

        // Act
        postTagRepository.addTagToPost(postId, tagId);

        // Assert
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_tags WHERE post_id = ? AND tag_id = ?",
                Integer.class,
                postId, tagId
        );
        assertEquals(1, count);
    }

    @Test
    void removeTagFromPost_ShouldDeleteAssociation() {
        // Arrange
        // Create a post
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Test Post", "Test Content", 0
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE title = ?",
                Long.class,
                "Test Post"
        );

        // Create a tag
        jdbcTemplate.update(
                "INSERT INTO tags (tag) VALUES (?)",
                "Java"
        );
        Long tagId = jdbcTemplate.queryForObject(
                "SELECT id FROM tags WHERE tag = ?",
                Long.class,
                "Java"
        );

        // Create association
        jdbcTemplate.update(
                "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                postId, tagId
        );

        // Act
        postTagRepository.removeTagFromPost(postId, tagId);

        // Assert
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM post_tags WHERE post_id = ? AND tag_id = ?",
                Integer.class,
                postId, tagId
        );
        assertEquals(0, count);
    }

    @Test
    void findTagIdsByPostId_ShouldReturnTagIds() {
        // Arrange
        // Create a post
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Test Post", "Test Content", 0
        );
        Long postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE title = ?",
                Long.class,
                "Test Post"
        );

        // Create tags
        jdbcTemplate.update(
                "INSERT INTO tags (tag) VALUES (?)",
                "Java"
        );
        Long tagId1 = jdbcTemplate.queryForObject(
                "SELECT id FROM tags WHERE tag = ?",
                Long.class,
                "Java"
        );

        jdbcTemplate.update(
                "INSERT INTO tags (tag) VALUES (?)",
                "Spring"
        );
        Long tagId2 = jdbcTemplate.queryForObject(
                "SELECT id FROM tags WHERE tag = ?",
                Long.class,
                "Spring"
        );

        // Create associations
        jdbcTemplate.update(
                "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                postId, tagId1
        );
        jdbcTemplate.update(
                "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                postId, tagId2
        );

        // Act
        List<Long> tagIds = postTagRepository.findTagIdsByPostId(postId);

        // Assert
        assertEquals(2, tagIds.size());
        assertTrue(tagIds.contains(tagId1));
        assertTrue(tagIds.contains(tagId2));
    }

    @Test
    void findPostIdsByTagId_ShouldReturnPostIds() {
        // Arrange
        // Create posts
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Post 1", "Content 1", 0
        );
        Long postId1 = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE title = ?",
                Long.class,
                "Post 1"
        );

        jdbcTemplate.update(
                "INSERT INTO posts (title, text, likes) VALUES (?, ?, ?)",
                "Post 2", "Content 2", 0
        );
        Long postId2 = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE title = ?",
                Long.class,
                "Post 2"
        );

        // Create a tag
        jdbcTemplate.update(
                "INSERT INTO tags (tag) VALUES (?)",
                "Java"
        );
        Long tagId = jdbcTemplate.queryForObject(
                "SELECT id FROM tags WHERE tag = ?",
                Long.class,
                "Java"
        );

        // Create associations
        jdbcTemplate.update(
                "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                postId1, tagId
        );
        jdbcTemplate.update(
                "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                postId2, tagId
        );

        // Act
        List<Long> postIds = postTagRepository.findPostIdsByTagId(tagId);

        // Assert
        assertEquals(2, postIds.size());
        assertTrue(postIds.contains(postId1));
        assertTrue(postIds.contains(postId2));
    }
} 