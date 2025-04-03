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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        List<PostDto> postDtos = postService.findAllWithPagination(pageNumber, pageSize, "id", "ASC").stream()
                .map(postDtoConverter::postToPostDto)
                .toList();
        long totalPosts = postService.countTotalPosts();
        model.addAttribute("posts", postDtos);
        model.addAttribute("paging", new Paging(pageNumber, pageSize, totalPosts));

        return "posts";
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

    @GetMapping("/posts/add")
    public String addPostGet() {
        return "add-post";
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable("id") Long postId, Model model) {
        Optional<Post> postOpt = postService.findById(postId);
        if (postOpt.isEmpty())
            return "posts";

        Post post = postOpt.get();
        PostDto postDto = postDtoConverter.postToPostDto(post);
        model.addAttribute("post", postDto);
        return "post";
    }

    @PostMapping("/posts/{id}")
    public String editPost(@PathVariable("id") Long postId,
                          @RequestParam("title") String title,
                          @RequestParam("text") String text,
                          @RequestParam(value = "image", required = false) MultipartFile imageFile,
                          @RequestParam(value = "tags", required = false) String tagsText) throws IOException {
        // Find the existing post
        Optional<Post> postOpt = postService.findById(postId);
        if (postOpt.isEmpty()) {
            return "redirect:/posts";
        }
        
        Post post = postOpt.get();
        
        // Update basic post fields
        post.setTitle(title);
        post.setText(text);
        
        // Handle image update if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            // Create and save new image
            Image image = new Image();
            image.setImage(imageFile.getBytes());
            image = imageRepository.save(image);
            
            // Update post with new image ID
            post.setImageId(image.getId());
        }
        
        // Save the updated post
        post = postService.save(post);
        
        // Handle tags update if provided
        if (tagsText != null && !tagsText.trim().isEmpty()) {
            // Get existing tags for this post
            List<Long> existingTagIds = postTagService.findTagIdsByPostId(postId);
            
            // Remove all existing tags
            for (Long tagId : existingTagIds) {
                postTagService.removeTagFromPost(postId, tagId);
            }
            
            // Add new tags
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
        
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/posts/{id}/edit")
    public String editPost(@PathVariable("id") Long postId, Model model) {
        Optional<Post> postOpt = postService.findById(postId);
        if (postOpt.isEmpty())
            return "redirect:/posts";

        Post post = postOpt.get();
        PostDto postDto = postDtoConverter.postToPostDto(post);
        model.addAttribute("post", postDto);
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
