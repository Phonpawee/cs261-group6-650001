package com.example.LoginTUgether.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LoginTUgether.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndCitizenId(String email, String citizenId);
    Optional<User> findByEmail(String email);
}

