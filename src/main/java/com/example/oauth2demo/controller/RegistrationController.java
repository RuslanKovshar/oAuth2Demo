package com.example.oauth2demo.controller;

import com.example.oauth2demo.dto.CreateUserDto;
import com.example.oauth2demo.entity.User;
import com.example.oauth2demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String registrationPage(@RequestParam(name = "error", required = false) String error) {
        System.out.println(error != null);
        return "registration";
    }

    @PostMapping
    public String createNewUser(CreateUserDto userDto) throws IOException {
      /*  File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdir();
        }*/

        boolean isCreated = userService.createNewUser(userDto);
        if (isCreated) {
            return "redirect:/secured";
        } else {
            return "redirect:/registration?error";
        }
    }
}
