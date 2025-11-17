package com.example.LoginTUgether.controller;

import com.example.LoginTUgether.model.User;
import com.example.LoginTUgether.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * List all users (for debug / admin).
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get user by student id.
     */
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<User> getByStudentId(@PathVariable String studentId) {
        Optional<User> userOpt = userRepository.findByStudentId(studentId);
        return userOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update role (ADMIN / STUDENT).
     * Body: { "role": "ADMIN" }
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id,
                                        @RequestBody Map<String, String> body) {

        String role = body.get("role");
        if (role == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "role is required"));
        }

        role = role.toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("STUDENT")) {
            return ResponseEntity.badRequest().body(Map.of("error", "role must be ADMIN or STUDENT"));
        }

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
}
