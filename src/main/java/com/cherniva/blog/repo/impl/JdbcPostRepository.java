package com.cherniva.blog.repo.impl;

import com.cherniva.blog.model.Post;
import com.cherniva.blog.model.Tag;
import com.cherniva.blog.repo.PostRepository;
import com.cherniva.blog.repo.PostTagRepository;
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
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate, 
                            PostTagRepository postTagRepository,
                            TagRepository tagRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.postTagRepository = postTagRepository;
        this.tagRepository = tagRepository;
        this.postRowMapper = (rs, rowNum) -> mapPost(rs);
    }

    private Post mapPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));
        post.setImage(rs.getBytes("image"));
        post.setLikes(rs.getInt("likes"));
        
        // Load tags for this post
        List<Long> tagIds = postTagRepository.findTagIdsByPostId(post.getId());
        List<Tag> tags = tagIds.stream()
            .map(tagId -> tagRepository.findById(tagId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
        post.setTags(tags);
        
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
        
        // Get the generated ID
        Long postId = jdbcTemplate.queryForObject(
            "SELECT LAST_INSERT_ID()",
            Long.class
        );
        post.setId(postId);
        
        // Save tags
        if (post.getTags() != null) {
            post.getTags().forEach(tag -> {
                if (tag.getId() == null) {
                    tag = tagRepository.save(tag);
                }
                postTagRepository.addTagToPost(postId, tag.getId());
            });
        }
        
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
        
        // Update tags
        if (post.getTags() != null) {
            // Remove existing tags
            List<Long> existingTagIds = postTagRepository.findTagIdsByPostId(post.getId());
            existingTagIds.forEach(tagId -> 
                postTagRepository.removeTagFromPost(post.getId(), tagId)
            );
            
            // Add new tags
            post.getTags().forEach(tag -> {
                if (tag.getId() == null) {
                    tag = tagRepository.save(tag);
                }
                postTagRepository.addTagToPost(post.getId(), tag.getId());
            });
        }
        
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
    @Transactional
    public void deleteById(Long id) {
        // First delete all tag associations
        List<Long> tagIds = postTagRepository.findTagIdsByPostId(id);
        tagIds.forEach(tagId -> postTagRepository.removeTagFromPost(id, tagId));
        
        // Then delete the post
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
} 