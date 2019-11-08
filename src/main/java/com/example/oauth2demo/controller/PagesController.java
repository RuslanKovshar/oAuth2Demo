package com.example.oauth2demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PagesController {

    @GetMapping
    public String welcomePage() {
        return "index";
    }

    @GetMapping("/secured")
    public String securedPage() {
        return "secure";
    }
}
