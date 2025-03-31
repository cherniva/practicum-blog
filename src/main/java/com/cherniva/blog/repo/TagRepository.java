package com.cherniva.blog.repo;

import com.cherniva.blog.model.Tag;

import java.util.List;

public interface TagRepository extends BlogRepository<Tag, Long> {
    List<Tag> findByPostId(Long postId);
}
