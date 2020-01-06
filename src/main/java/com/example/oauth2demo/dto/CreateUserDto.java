package com.example.oauth2demo.dto;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public class CreateUserDto {

    private String login;
    private String email;
    private String password;
    private MultipartFile file;

    public CreateUserDto(String login, String email, String password, MultipartFile file) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.file = file;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
