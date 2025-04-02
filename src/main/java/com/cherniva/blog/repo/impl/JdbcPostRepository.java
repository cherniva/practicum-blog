package com.cherniva.blog.repo.impl;

import com.cherniva.blog.model.Post;
import com.cherniva.blog.repo.PostRepository;
import com.cherniva.blog.repo.TagRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPostRepository implements PostRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Post> postRowMapper;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate,
                            TagRepository tagRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.postRowMapper = (rs, rowNum) -> mapPost(rs);
    }

    private Post mapPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));
        post.setImage(rs.getBytes("image"));
        post.setLikes(rs.getInt("likes"));
        
        return post;
    }

    @Override
    @Transactional
    public Post save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        } else {
            return update(post);
        }
    }

    private Post insert(Post post) {
        jdbcTemplate.update(
            "INSERT INTO posts (title, text, image, likes) VALUES (?, ?, ?, ?)",
            post.getTitle(),
            post.getText(),
            post.getImage(),
            post.getLikes()
        );
        
        return post;
    }

    private Post update(Post post) {
        jdbcTemplate.update(
            "UPDATE posts SET title = ?, text = ?, image = ?, likes = ? WHERE id = ?",
            post.getTitle(),
            post.getText(),
            post.getImage(),
            post.getLikes(),
            post.getId()
        );
        
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        List<Post> results = jdbcTemplate.query(
            "SELECT * FROM posts WHERE id = ?",
            postRowMapper,
            id
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public List<Post> findAll() {
        return jdbcTemplate.query("SELECT * FROM posts", postRowMapper);
    }

    @Override
    public List<Post> findAllWithPagination(int offset, int limit, String sortBy, String sortDirection) {
        // Validate sortBy to prevent SQL injection
        String validSortBy = switch (sortBy.toLowerCase()) {
            case "title", "likes", "id" -> sortBy;
            default -> "id"; // Default sorting
        };
        
        // Validate sortDirection
        String validSortDirection = "desc".equalsIgnoreCase(sortDirection) ? "DESC" : "ASC";
        
        String sql = String.format(
            "SELECT * FROM posts ORDER BY %s %s LIMIT ? OFFSET ?",
            validSortBy,
            validSortDirection
        );
        
        return jdbcTemplate.query(
            sql,
            postRowMapper,
            limit,
            offset
        );
    }

    @Override
    public long countTotalPosts() {
        return Optional.ofNullable(
                        jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM posts",
                                Long.class
                        ))
                .orElse(0L);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    @Override
    public List<Post> findByTags(List<Long> tagIds) {
        return jdbcTemplate.query(
            "SELECT DISTINCT p.* FROM posts p " +
            "JOIN post_tags pt ON p.id = pt.post_id " +
            "WHERE pt.tag_id IN (?)",
            postRowMapper,
            tagIds
        );
    }

    @Override
    public List<Post> findByTitle(String title) {
        return jdbcTemplate.query(
                "SELECT * FROM posts WHERE title = ?",
                postRowMapper,
                title
        );
    }
} 