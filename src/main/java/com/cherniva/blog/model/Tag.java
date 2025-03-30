package com.cherniva.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    private Long id;
    private String tag;
    private List<Post> posts;
}
