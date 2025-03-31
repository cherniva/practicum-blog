package com.cherniva.blog.repo;

import com.cherniva.blog.model.Comment;

import java.util.List;

public interface CommentRepository extends BlogRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}
