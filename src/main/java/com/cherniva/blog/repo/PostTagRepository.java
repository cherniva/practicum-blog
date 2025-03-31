package com.cherniva.blog.repo;

import java.util.List;

public interface PostTagRepository {
    void addTagToPost(Long postId, Long tagId);
    void removeTagFromPost(Long postId, Long tagId);
    List<Long> findTagIdsByPostId(Long postId);
    List<Long> findPostIdsByTagId(Long tagId);
} 