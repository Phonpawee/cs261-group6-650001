package com.example.LoginTUgether.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.LoginTUgether.model.User;
import com.example.LoginTUgether.repo.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") 
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/findOrCreate")
    public ResponseEntity<Map<String, Object>> findOrCreateUser(@RequestBody Map<String, String> userData) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = userData.get("email");
            String name = userData.get("name");
            String studentId = userData.get("studentId");
            
            if (email == null || email.isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<User> existingUser = userRepository.findByEmail(email);
            
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                response.put("isNew", false);
            } else {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setCitizenId(studentId); 
                user = userRepository.save(user);
                response.put("isNew", true);
            }
            
            response.put("success", true);
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        String email = request.get("email");
        String citizenId = request.get("citizenId");
        
        Optional<User> user = userRepository.findByEmailAndCitizenId(email, citizenId);
        
        if (user.isPresent()) {
            response.put("success", true);
            response.put("message", "Login success");
            response.put("userId", user.get().getId());
            response.put("email", user.get().getEmail());
            response.put("name", user.get().getName());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }
}