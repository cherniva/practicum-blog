package com.cherniva.blog.repo;

import com.cherniva.blog.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends BlogRepository<Tag, Long> {
    List<Tag> findByPostId(Long postId);
    Optional<Tag> findByTag(String tag);
}
