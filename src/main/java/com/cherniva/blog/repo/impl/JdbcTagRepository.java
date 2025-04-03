package com.cherniva.blog.repo.impl;

import com.cherniva.blog.model.Tag;
import com.cherniva.blog.repo.TagRepository;
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
public class JdbcTagRepository implements TagRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Tag> tagRowMapper;

    public JdbcTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagRowMapper = (rs, rowNum) -> mapTag(rs);
    }

    private Tag mapTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getLong("id"));
        tag.setTag(rs.getString("tag"));
        return tag;
    }

    @Override
    public Tag save(Tag tag) {
        if (tag.getId() == null) {
            return insert(tag);
        } else {
            return update(tag);
        }
    }

    private Tag insert(Tag tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO tags (tag) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, tag.getTag());
            return ps;
        }, keyHolder);
        
        tag.setId(keyHolder.getKey().longValue());
        return tag;
    }

    private Tag update(Tag tag) {
        jdbcTemplate.update(
            "UPDATE tags SET tag = ? WHERE id = ?",
            tag.getTag(),
            tag.getId()
        );
        return tag;
    }

    @Override
    public Optional<Tag> findById(Long id) {
        List<Tag> results = jdbcTemplate.query(
            "SELECT * FROM tags WHERE id = ?",
            tagRowMapper,
            id
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public List<Tag> findAll() {
        return jdbcTemplate.query("SELECT * FROM tags", tagRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM tags WHERE id = ?", id);
    }

    @Override
    public List<Tag> findByPostId(Long postId) {
        return jdbcTemplate.query(
            "SELECT t.* FROM tags t " +
            "JOIN post_tags pt ON t.id = pt.tag_id " +
            "WHERE pt.post_id = ?",
            tagRowMapper,
            postId
        );
    }
} 