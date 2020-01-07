package com.example.oauth2demo.service;

import com.example.oauth2demo.dto.CreateUserDto;
import com.example.oauth2demo.entity.User;
import com.example.oauth2demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Value("${upload.path}")
    private String uploadPath;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByEmail(s).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean createNewUser(CreateUserDto userDto) {
        userDto.makePath(uploadPath);
        String avatarPath = userDto.getFilePath();
        User user = new User(userDto.getLogin(),
                userDto.getEmail(),
                encoder.encode(userDto.getPassword()),
                "img/" + avatarPath);
        try {
            userRepository.save(user);

            MultipartFile file = userDto.getFile();
            if (file != null) {
                file.transferTo(new File(avatarPath));

                user.setAvatarPath(avatarPath);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
