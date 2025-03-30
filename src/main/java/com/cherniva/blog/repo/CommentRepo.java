package com.cherniva.blog.repo;

import com.cherniva.blog.model.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepo extends CrudRepository<Comment, Long> {
}
