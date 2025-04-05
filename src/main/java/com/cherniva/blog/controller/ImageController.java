package com.cherniva.blog.controller;

import com.cherniva.blog.model.Image;
import com.cherniva.blog.repo.ImageRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/images")
public class ImageController {
    private final ImageRepository imageRepository;

    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long postId) throws IOException {
        Optional<Image> imageOpt = imageRepository.findByPostId(postId);
        if (imageOpt.isEmpty()) {
            return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
        }

        Image image = imageOpt.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.getImage().length);
        return new ResponseEntity<>(image.getImage(), headers, HttpStatus.OK);
    }
}
