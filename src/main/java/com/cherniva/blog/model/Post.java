package com.cherniva.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String text;
    private byte[] image;
    private int likes;
    private List<Tag> tags;
    private List<Comment> comments;
}
