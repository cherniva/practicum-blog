package com.cherniva.blog.controller;

import com.cherniva.blog.service.PostService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/images")
public class ImageController {
    private final PostService postService;

    public ImageController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) throws IOException {
        Path imagePath = Paths.get("/Users/vokinrehc/Pictures/bird.jpg");
        
        if (!Files.exists(imagePath)) {
            throw new IOException("Image file not found: " + imagePath);
        }
        
        byte[] imageBytes = Files.readAllBytes(imagePath);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(imageBytes.length);
        
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
