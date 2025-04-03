package com.cherniva.blog.repo.impl;

import com.cherniva.blog.model.Comment;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.repo.CommentRepository;
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
public class JdbcCommentRepository implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Comment> commentRowMapper;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.commentRowMapper = (rs, rowNum) -> mapComment(rs);
    }

    private Comment mapComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setComment(rs.getString("comment"));
        comment.setPostId(rs.getLong("post_id"));
        
        return comment;
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            return insert(comment);
        } else {
            return update(comment);
        }
    }

    private Comment insert(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO comments (comment, post_id) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, comment.getComment());
            ps.setLong(2, comment.getPostId());
            return ps;
        }, keyHolder);
        
        comment.setId(keyHolder.getKey().longValue());
        return comment;
    }

    private Comment update(Comment comment) {
        jdbcTemplate.update(
            "UPDATE comments SET comment = ?, post_id = ? WHERE id = ?",
            comment.getComment(),
            comment.getPostId(),
            comment.getId()
        );
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        List<Comment> results = jdbcTemplate.query(
            "SELECT * FROM comments WHERE id = ?",
            commentRowMapper,
            id
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public List<Comment> findAll() {
        return jdbcTemplate.query("SELECT * FROM comments", commentRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM comments WHERE id = ?", id);
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return jdbcTemplate.query(
            "SELECT * FROM comments WHERE post_id = ?",
            commentRowMapper,
            postId
        );
    }
} 