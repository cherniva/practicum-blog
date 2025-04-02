package com.cherniva.blog.service;

import com.cherniva.blog.repo.PostTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostTagService {
    private final PostTagRepository postTagRepository;

    public PostTagService(PostTagRepository postTagRepository) {
        this.postTagRepository = postTagRepository;
    }

    public void addTagToPost(Long postId, Long tagId) {
        postTagRepository.addTagToPost(postId, tagId);
    }

    public void removeTagFromPost(Long postId, Long tagId) {
        postTagRepository.removeTagFromPost(postId, tagId);
    }

    public List<Long> findTagIdsByPostId(Long postId) {
        return postTagRepository.findTagIdsByPostId(postId);
    }

    public List<Long> findPostIdsByTagId(Long tagId) {
        return postTagRepository.findPostIdsByTagId(tagId);
    }

} 