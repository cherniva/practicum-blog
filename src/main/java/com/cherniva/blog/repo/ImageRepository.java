package com.cherniva.blog.repo;

import com.cherniva.blog.model.Image;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends BlogRepository<Image, Long> {
    Optional<Image> findByPostId(Long postId);
}
