package com.example.LoginTUgether.controller;

import com.example.LoginTUgether.model.Event;
import com.example.LoginTUgether.repo.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    
    @Autowired
    private EventRepository eventRepository;
    
    
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Event>> getEventsByCategory(@PathVariable String category) {
        List<Event> events = eventRepository.findByCategory(category);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String keyword) {
        List<Event> events = eventRepository.findByNameContainingIgnoreCase(keyword);
        return ResponseEntity.ok(events);
    }
    
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.ok(savedEvent);
    }
    
    @GetMapping("/my-events/{userId}")
    public ResponseEntity<List<Event>> getMyEvents(@PathVariable Long userId) {
        List<Event> events = eventRepository.findByOrganizerId(userId);
        return ResponseEntity.ok(events);
    }
}