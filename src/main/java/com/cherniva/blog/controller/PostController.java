package com.cherniva.blog.controller;

import com.cherniva.blog.converter.PostDtoConverter;
import com.cherniva.blog.dto.PostDto;
import com.cherniva.blog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller("/blog")
public class PostController {
    private final PostService postService;
    private final PostDtoConverter postDtoConverter;

    public PostController(PostService postService, PostDtoConverter postDtoConverter) {
        this.postService = postService;
        this.postDtoConverter = postDtoConverter;
    }

    @GetMapping({"", "/", "/posts"})
    public String allPosts(Model model) {
        List<PostDto> postDtos = postService.findAll().stream()
                .map(postDtoConverter::postToPostDto)
                .toList();
        model.addAttribute("posts", postDtos);
        return "posts";
    }

    @GetMapping("/posts/add")
    public String addPost() {
        return "add-post";
    }
}
