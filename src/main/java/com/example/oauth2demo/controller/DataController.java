package com.example.oauth2demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class DataController {

    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }
}
