package com.cherniva.blog.repo;

import com.cherniva.blog.model.Post;

import java.util.List;

public interface PostRepository extends BlogRepository<Post, Long> {
    List<Post> findByTags(List<Long> tagIds);
    List<Post> findByTitle(String title);
    
    // Add pagination methods with sorting
    List<Post> findAllWithPagination(int offset, int limit, String sortBy, String sortDirection);
    long countTotalPosts();
}
