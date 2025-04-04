package com.cherniva.blog.controller;

import com.cherniva.blog.model.Comment;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.service.CommentService;
import com.cherniva.blog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class CommentController {
    private final PostService postService;
    private final CommentService commentService;

    public CommentController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable("id") Long postId, @RequestParam("text") String commentText) {
        Optional<Post> postOpt = postService.findById(postId);
        if (postOpt.isEmpty()) {
            return "redirect:/posts";
        }

        Comment comment = new Comment();
        comment.setComment(commentText);
        comment.setPostId(postId);
        commentService.save(comment);

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}")
    public String editComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId,
                              @RequestParam("text") String commentText) {
        Optional<Comment> commentOpt = commentService.findById(commentId);
        if (commentOpt.isEmpty()) {
            return "redirect:/posts/" + postId;
        }

        Comment comment = commentOpt.get();
        comment.setComment(commentText);
        commentService.save(comment);

        return "redirect:/posts/" + postId;
    }



    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        commentService.deleteById(commentId);

        return "redirect:/posts/" + postId;
    }
}
