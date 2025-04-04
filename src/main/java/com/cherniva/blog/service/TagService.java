package com.cherniva.blog.service;

import com.cherniva.blog.model.Tag;
import com.cherniva.blog.repo.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public void deleteById(Long id) {
        tagRepository.deleteById(id);
    }

    public List<Tag> findByPostId(Long postId) {
        return tagRepository.findByPostId(postId);
    }

    public Optional<Tag> findByTag(String tag) {
        return tagRepository.findByTag(tag);
    }
}