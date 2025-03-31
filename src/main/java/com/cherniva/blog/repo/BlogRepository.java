package com.cherniva.blog.repo;

import com.cherniva.blog.model.Comment;

import java.util.List;
import java.util.Optional;

public interface BlogRepository<T, ID> {
    T save(T t);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}
