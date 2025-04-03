package com.cherniva.blog.converter;

import com.cherniva.blog.dto.PostDto;
import com.cherniva.blog.model.Comment;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.model.Tag;
import com.cherniva.blog.service.CommentService;
import com.cherniva.blog.service.TagService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PostDtoConverter {
    private final CommentService commentService;
    private final TagService tagService;

    public PostDtoConverter(CommentService commentService, TagService tagService) {
        this.commentService = commentService;
        this.tagService = tagService;
    }

    public PostDto postToPostDto(Post post) {
        Long postId = post.getId();
        List<Comment> comments = commentService.findByPostId(postId);
        List<Tag> tags = tagService.findByPostId(postId);

        List<String> paragraphs = getParagraphs(post.getText());
        String preview = paragraphs.isEmpty() ? "" : paragraphs.get(0);

        PostDto postDto = new PostDto();
        postDto.setPostId(postId);
        postDto.setTitle(post.getTitle() != null ? post.getTitle() : "");
        postDto.setTextPreview(preview);
        postDto.setTextParts(paragraphs);
        postDto.setLikesCount(post.getLikes());
        postDto.setComments(comments != null ? comments : new ArrayList<>());
        postDto.setTags(tags != null ? tags : new ArrayList<>());

        return postDto;
    }

    private List<String> getParagraphs(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // Split by multiple possible paragraph separators
        return Arrays.asList(text.split("(?m)(?:\r\n|\n|\r){2,}"));
    }

}
