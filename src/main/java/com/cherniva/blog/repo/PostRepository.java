package com.cherniva.blog.repo;

import com.cherniva.blog.model.Post;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    @Query("SELECT p.* FROM posts p " +
           "JOIN post_tags pt ON p.id = pt.post_id " +
           "WHERE pt.tag_id IN (:tagIds)")
    List<Post> findByTags(@Param("tagIds") List<Long> tagIds);
}
