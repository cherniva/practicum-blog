package com.cherniva.blog.controller;

import com.cherniva.blog.model.Post;
import com.cherniva.blog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

//    @GetMapping("/saveNewPost")
//    @ResponseBody
//    public String saveNewPost() {
//        Post post = new Post();
//        post.setTitle("Test post");
//        post.setText("Test post text");
//
//        post = postService.save(post);
//
//        return String.format("Save post with id %d", post.getId());
//    }
}
