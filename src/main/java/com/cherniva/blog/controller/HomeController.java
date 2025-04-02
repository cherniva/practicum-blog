package com.cherniva.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @GetMapping("/test1")
    @ResponseBody
    public String home() {
        return "<h1>Blog home page</h1>";
    }
}
