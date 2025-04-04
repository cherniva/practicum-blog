package com.cherniva.blog.service;

import com.cherniva.blog.model.Post;
import com.cherniva.blog.model.Tag;
import com.cherniva.blog.repo.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public List<Post> findAllWithPagination(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        int offset = (pageNumber - 1) * pageSize;
        return postRepository.findAllWithPagination(offset, pageSize, sortBy, sortDirection);
    }

    public long countTotalPosts() {
        return postRepository.countTotalPosts();
    }

    public List<Post> findByTags(List<Tag> tags) {
        List<Long> tagIds = tags.stream()
                .map(Tag::getId)
                .toList();
        return postRepository.findByTags(tagIds);
    }

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}
