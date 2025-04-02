package com.cherniva.blog.service;

import com.cherniva.blog.model.Like;
import com.cherniva.blog.repo.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public Like save(Like like) {
        return likeRepository.save(like);
    }

    public Optional<Like> findById(Long id) {
        return likeRepository.findById(id);
    }

    public List<Like> findAll() {
        return likeRepository.findAll();
    }

    public void deleteById(Long id) {
        likeRepository.deleteById(id);
    }

    public List<Like> findByPostId(Long postId) {
        return likeRepository.findByPostId(postId);
    }
} 