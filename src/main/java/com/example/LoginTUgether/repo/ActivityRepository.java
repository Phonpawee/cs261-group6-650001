package com.example.LoginTUgether.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LoginTUgether.model.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
}