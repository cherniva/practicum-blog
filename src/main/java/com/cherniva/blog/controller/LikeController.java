package com.cherniva.blog.controller;

import com.cherniva.blog.model.Like;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.service.LikeService;
import com.cherniva.blog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LikeController {
    private final PostService postService;
    private final LikeService likeService;

    public LikeController(PostService postService, LikeService likeService) {
        this.postService = postService;
        this.likeService = likeService;
    }

    @PostMapping("/posts/{id}/like")
    public String likePost(@PathVariable("id") Long postId, @RequestParam(name = "like") boolean likeFlag) {
        Optional<Post> postOpt = postService.findById(postId);
        if (postOpt.isEmpty()) {
            return "redirect:/posts";
        }

        Post post = postOpt.get();

        if (likeFlag) {
            Like like = new Like();
            like.setPostId(postId);
            likeService.save(like);

            post.setLikes(post.getLikes() + 1);
        } else {
            // todo: add dislike
            post.setLikes(Math.max(0, post.getLikes() - 1));
        }
        postService.save(post);

        return "redirect:/posts/" + postId;
    }
}
