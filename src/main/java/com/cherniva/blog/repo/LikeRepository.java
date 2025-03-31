package com.cherniva.blog.repo;

import com.cherniva.blog.model.Like;

import java.util.List;

public interface LikeRepository extends BlogRepository<Like, Long> {
    List<Like> findByPostId(Long postId);
}