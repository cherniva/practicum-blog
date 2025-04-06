package com.cherniva.blog.repo;

import com.cherniva.blog.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TagRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void save_ShouldSaveTag() {
        // Arrange
        Tag tag = new Tag();
        tag.setTag("Java");

        // Act
        Tag savedTag = tagRepository.save(tag);

        // Assert
        assertNotNull(savedTag.getId());
        assertEquals("Java", savedTag.getTag());

        // Verify in database
        Tag dbTag = jdbcTemplate.queryForObject(
                "SELECT * FROM tags WHERE id = ?",
                (rs, rowNum) -> {
                    Tag t = new Tag();
                    t.setId(rs.getLong("id"));
                    t.setTag(rs.getString("tag"));
                    return t;
                },
                savedTag.getId()
        );
        assertNotNull(dbTag);
        assertEquals(savedTag.getTag(), dbTag.getTag());
    }

    @Test
    void findById_WhenTagExists_ShouldReturnTag() {
        // Arrange
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
        Optional<Tag> foundTag = tagRepository.findById(tagId);

        // Assert
        assertTrue(foundTag.isPresent());
        assertEquals(tagId, foundTag.get().getId());
        assertEquals("Java", foundTag.get().getTag());
    }

    @Test
    void findById_WhenTagDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Tag> foundTag = tagRepository.findById(999L);

        // Assert
        assertFalse(foundTag.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllTags() {
        // Arrange
        jdbcTemplate.update(
                "INSERT INTO tags (tag) VALUES (?)",
                "Java"
        );
        jdbcTemplate.update(
                "INSERT INTO tags (tag) VALUES (?)",
                "Spring"
        );

        // Act
        List<Tag> tags = tagRepository.findAll();

        // Assert
        assertEquals(2, tags.size());
    }

    @Test
    void deleteById_ShouldDeleteTag() {
        // Arrange
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
        tagRepository.deleteById(tagId);

        // Assert
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tags WHERE id = ?",
                Integer.class,
                tagId
        );
        assertEquals(0, count);
    }

    @Test
    void findByPostId_ShouldReturnTagsForPost() {
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

        // Associate tags with post
        jdbcTemplate.update(
                "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                postId, tagId1
        );
        jdbcTemplate.update(
                "INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                postId, tagId2
        );

        // Act
        List<Tag> tags = tagRepository.findByPostId(postId);

        // Assert
        assertEquals(2, tags.size());
        assertTrue(tags.stream().anyMatch(t -> t.getTag().equals("Java")));
        assertTrue(tags.stream().anyMatch(t -> t.getTag().equals("Spring")));
    }

    @Test
    void findByTag_WhenTagExists_ShouldReturnTag() {
        // Arrange
        jdbcTemplate.update(
                "INSERT INTO tags (tag) VALUES (?)",
                "Java"
        );

        // Act
        Optional<Tag> foundTag = tagRepository.findByTag("Java");

        // Assert
        assertTrue(foundTag.isPresent());
        assertEquals("Java", foundTag.get().getTag());
    }

    @Test
    void findByTag_WhenTagDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Tag> foundTag = tagRepository.findByTag("NonExistent");

        // Assert
        assertFalse(foundTag.isPresent());
    }
} 