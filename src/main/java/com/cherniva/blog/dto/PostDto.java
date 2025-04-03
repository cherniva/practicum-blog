package com.cherniva.blog.dto;

import com.cherniva.blog.model.Comment;
import com.cherniva.blog.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long postId;
    private String title;
    private String textPreview;
    private String text;
    private int likesCount;
    private List<Comment> comments;
    private List<Tag> tags;
}
