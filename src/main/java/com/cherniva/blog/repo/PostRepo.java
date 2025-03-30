package com.cherniva.blog.repo;

import com.cherniva.blog.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepo extends CrudRepository<Post, Long> {
}
