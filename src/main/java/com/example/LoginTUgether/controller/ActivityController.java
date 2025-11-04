package com.example.LoginTUgether.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.LoginTUgether.dto.ActivityRequestDTO;
import com.example.LoginTUgether.model.Activity;
import com.example.LoginTUgether.service.ActivityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/create")
    public ResponseEntity<Activity> createActivity(@Valid @RequestBody ActivityRequestDTO request) {
        // map DTO -> entity
        Activity a = new Activity();
        a.setName(request.getName());
        a.setCategory(request.getCategory());
        a.setDate(request.getDate());
        a.setTime(request.getName());
        a.setMaxSeats(request.getMaxSeats());
        a.setDescription(request.getDescription());
        a.setOrganizerEmail(request.getOrganizerEmail());

        Activity saved = activityService.createActivity(a);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Activity>> getAll() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivityById(id));
    }

    // soft cancel (recommended)
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Activity> cancel(@PathVariable Long id) {
        Activity updated = activityService.cancelActivity(id);
        return ResponseEntity.ok(updated);
    }

    // hard delete (admin only) - optional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}