package com.cherniva.blog.controller;

import com.cherniva.blog.converter.PostDtoConverter;
import com.cherniva.blog.dto.PostDto;
import com.cherniva.blog.model.Image;
import com.cherniva.blog.model.Post;
import com.cherniva.blog.model.Tag;
import com.cherniva.blog.repo.ImageRepository;
import com.cherniva.blog.service.PostService;
import com.cherniva.blog.service.PostTagService;
import com.cherniva.blog.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PostController {
    private final PostService postService;
    private final PostDtoConverter postDtoConverter;
    private final ImageRepository imageRepository;
    private final TagService tagService;
    private final PostTagService postTagService;

    public PostController(PostService postService, 
                         PostDtoConverter postDtoConverter,
                         ImageRepository imageRepository,
                         TagService tagService,
                         PostTagService postTagService) {
        this.postService = postService;
        this.postDtoConverter = postDtoConverter;
        this.imageRepository = imageRepository;
        this.tagService = tagService;
        this.postTagService = postTagService;
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

    @GetMapping("/post/add")
    public String addPostGet() {
        return "add-post";
    }

    @PostMapping("/posts")
    public String addPost(@RequestParam("title") String title,
                         @RequestParam("text") String text,
                         @RequestParam(value = "image", required = false) MultipartFile imageFile,
                         @RequestParam(value = "tags", required = false) String tagsText) throws IOException {
        // Create and save image if provided
        Long imageId = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = new Image();
            image.setImage(imageFile.getBytes());
            image = imageRepository.save(image);
            imageId = image.getId();
        }

        // Create and save post
        Post post = new Post();
        post.setTitle(title);
        post.setText(text);
        post.setImageId(imageId);
        post.setLikes(0);
        post = postService.save(post);

        // Handle tags if provided
        if (tagsText != null && !tagsText.trim().isEmpty()) {
            List<String> tagNames = Arrays.stream(tagsText.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            for (String tagName : tagNames) {
                // Create or find existing tag
                Tag tag = new Tag();
                tag.setTag(tagName);
                tag = tagService.save(tag);

                // Associate tag with post
                postTagService.addTagToPost(post.getId(), tag.getId());
            }
        }

        return "redirect:/posts";
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
