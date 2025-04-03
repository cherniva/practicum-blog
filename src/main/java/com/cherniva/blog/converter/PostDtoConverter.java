package com.cherniva.blog.converter;

import com.cherniva.blog.dto.PostDto;
import com.cherniva.blog.model.Comment;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.model.Tag;
import com.cherniva.blog.service.CommentService;
import com.cherniva.blog.service.TagService;
import org.springframework.stereotype.Service;

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

        String postText = post.getText();
        String preview = getFirstParagraph(postText);

        PostDto postDto = new PostDto();
        postDto.setPostId(postId);
        postDto.setTitle(post.getTitle());
        postDto.setTextPreview(preview);
        postDto.setText(postText);
        postDto.setLikesCount(post.getLikes());
        postDto.setComments(comments);
        postDto.setTags(tags);

        return postDto;
    }

    private String getFirstParagraph(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // Split by multiple possible paragraph separators
        String[] paragraphs = text.split("(?m)(?:\r\n|\n|\r){2,}");
        return paragraphs[0].trim();
    }

}
