package com.cherniva.blog.repo.impl;

import com.cherniva.blog.repo.PostTagRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcPostTagRepository implements PostTagRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcPostTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addTagToPost(Long postId, Long tagId) {
        jdbcTemplate.update(
            "INSERT IGNORE INTO post_tags (post_id, tag_id) VALUES (?, ?)",
            postId,
            tagId
        );
    }

    @Override
    public void removeTagFromPost(Long postId, Long tagId) {
        jdbcTemplate.update(
            "DELETE FROM post_tags WHERE post_id = ? AND tag_id = ?",
            postId,
            tagId
        );
    }

    @Override
    public List<Long> findTagIdsByPostId(Long postId) {
        return jdbcTemplate.queryForList(
            "SELECT tag_id FROM post_tags WHERE post_id = ?",
            Long.class,
            postId
        );
    }

    @Override
    public List<Long> findPostIdsByTagId(Long tagId) {
        return jdbcTemplate.queryForList(
            "SELECT post_id FROM post_tags WHERE tag_id = ?",
            Long.class,
            tagId
        );
    }
} 