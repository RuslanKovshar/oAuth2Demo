package com.example.oauth2demo.controller;

import com.example.oauth2demo.dto.CreateUserDto;
import com.example.oauth2demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;

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
    public String createNewUser(CreateUserDto userDto/*, @RequestParam(name = "file") MultipartFile file*/) {
        System.out.println(userDto.getFile().getOriginalFilename());
        boolean isCreated = userService.createNewUser(userDto);
        if (isCreated) {
            return "redirect:";
        } else {
            return "redirect:/registration?error";
        }

    }
}
