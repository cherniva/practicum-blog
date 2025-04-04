package com.cherniva.blog.controller;

import com.cherniva.blog.model.Comment;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.service.CommentService;
import com.cherniva.blog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.IllegalArgumentException;

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
        Post post = postService.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post does not exist"));

        Comment comment = new Comment();
        comment.setComment(commentText);
        comment.setPostId(postId);
        commentService.save(comment);

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}")
    public String editComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId,
                              @RequestParam("text") String commentText) {
        Comment comment = commentService.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment does not exist"));
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
