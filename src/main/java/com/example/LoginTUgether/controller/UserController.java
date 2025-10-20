package com.example.LoginTUgether.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.LoginTUgether.repo.UserRepository;
import com.example.LoginTUgether.req.LoginRequest;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") 
public class UserController {

    private final UserRepository userRepository;

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        var user = userRepository.findByEmailAndCitizenId(
                request.getEmail(),
                request.getCitizenId()
        );
        if (user.isPresent()) {
            return "Login success";
        } else {
            return "Invalid credentials";
        }
    }
}