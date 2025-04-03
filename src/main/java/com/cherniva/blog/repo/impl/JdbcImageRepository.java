package com.cherniva.blog.repo.impl;

import com.cherniva.blog.model.Image;
import com.cherniva.blog.repo.ImageRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcImageRepository implements ImageRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Image> imageRowMapper;

    public JdbcImageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.imageRowMapper = (rs, rowNum) -> mapImage(rs);
    }

    private Image mapImage(ResultSet rs) throws SQLException {
        Image image = new Image();
        image.setId(rs.getLong("id"));
        
        // Convert BLOB to byte array
        java.sql.Blob blob = rs.getBlob("image");
        if (blob != null) {
            image.setImage(blob.getBytes(1, (int) blob.length()));
        } else {
            image.setImage(new byte[0]);
        }

        return image;
    }

    @Override
    public Image save(Image image) {
        if (image.getId() == null) {
            return insert(image);
        } else {
            return update(image);
        }
    }

    private Image insert(Image image) {
        jdbcTemplate.update(
                "INSERT INTO images (image) VALUES (?)",
                image.getImage()
        );
        return image;
    }

    private Image update(Image image) {
        jdbcTemplate.update(
                "UPDATE images SET image = ? WHERE id = ?",
                image.getImage(),
                image.getId()
        );
        return image;
    }

    @Override
    public Optional<Image> findById(Long id) {
        List<Image> results = jdbcTemplate.query(
                "SELECT * FROM images WHERE id = ?",
                imageRowMapper,
                id
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public List<Image> findAll() {
        return jdbcTemplate.query("SELECT * FROM images", imageRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM images WHERE id = ?", id);
    }
    
    @Override
    public Optional<Image> findByPostId(Long postId) {
        List<Image> results = jdbcTemplate.query(
                "SELECT i.* FROM images i " +
                "JOIN posts p ON i.id = p.image_id " +
                "WHERE p.id = ?",
                imageRowMapper,
                postId
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }
}
