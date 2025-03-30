package com.cherniva.blog.repo;

import com.cherniva.blog.model.Like;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepo extends CrudRepository<Like, Long> {
} 