package com.cherniva.blog.repo.impl;

import com.cherniva.blog.model.Like;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.repo.LikeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcLikeRepository implements LikeRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Like> likeRowMapper;

    public JdbcLikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.likeRowMapper = (rs, rowNum) -> mapLike(rs);
    }

    private Like mapLike(ResultSet rs) throws SQLException {
        Like like = new Like();
        like.setId(rs.getLong("id"));
        like.setPostId(rs.getLong("post_id"));
        
        return like;
    }

    @Override
    public Like save(Like like) {
        if (like.getId() == null) {
            return insert(like);
        } else {
            return update(like);
        }
    }

    private Like insert(Like like) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO likes (post_id) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, like.getPostId());
            return ps;
        }, keyHolder);
        
        like.setId(keyHolder.getKey().longValue());
        return like;
    }

    private Like update(Like like) {
        jdbcTemplate.update(
            "UPDATE likes SET post_id = ? WHERE id = ?",
            like.getPostId(),
            like.getId()
        );
        return like;
    }

    @Override
    public Optional<Like> findById(Long id) {
        List<Like> results = jdbcTemplate.query(
            "SELECT * FROM likes WHERE id = ?",
            likeRowMapper,
            id
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public List<Like> findAll() {
        return jdbcTemplate.query("SELECT * FROM likes", likeRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM likes WHERE id = ?", id);
    }

    @Override
    public List<Like> findByPostId(Long postId) {
        return jdbcTemplate.query(
            "SELECT * FROM likes WHERE post_id = ?",
            likeRowMapper,
            postId
        );
    }
} 