package com.cherniva.blog.controller;

import com.cherniva.blog.converter.PostDtoConverter;
import com.cherniva.blog.dto.PostDto;
import com.cherniva.blog.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller("/blog")
public class PostController {
    private final PostService postService;
    private final PostDtoConverter postDtoConverter;

    public PostController(PostService postService, PostDtoConverter postDtoConverter) {
        this.postService = postService;
        this.postDtoConverter = postDtoConverter;
    }

    @GetMapping({"", "/", "/home", "/posts"})
    public String allPosts(Model model,
                           @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
                           @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
                        //    @RequestParam(name = "sortBy", defaultValue = "id") String sortBy, // TODO: implement sorting
                        //    @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection
        List<PostDto> postDtos = postService.findAllWithPagination(pageNumber, pageSize, "id", "ASC").stream()
                .map(postDtoConverter::postToPostDto)
                .toList();
        long totalPosts = postService.countTotalPosts();
        model.addAttribute("posts", postDtos);
        model.addAttribute("paging", new Paging(pageNumber, pageSize, totalPosts));
        // model.addAttribute("sortBy", sortBy);
        // model.addAttribute("sortDirection", sortDirection);
        return "posts";
    }

    @GetMapping("/posts/add")
    public String addPost() {
        return "add-post";
    }

    private record Paging(int pageNumber, int pageSize, long totalItems) {
        public boolean hasPrevious() {
            return pageNumber > 1;
        }

        public boolean hasNext() {
            return pageNumber * pageSize < totalItems;
        }
    }
}
