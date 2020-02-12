package com.example.oauth2demo.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class DataController {

    @ApiOperation(value = "View info about authenticated user")
    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }
}
