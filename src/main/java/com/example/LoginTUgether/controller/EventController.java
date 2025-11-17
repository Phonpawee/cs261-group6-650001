package com.example.LoginTUgether.controller;

import com.example.LoginTUgether.model.Event;
import com.example.LoginTUgether.model.User;
import com.example.LoginTUgether.repo.EventRepository;
import com.example.LoginTUgether.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

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

    @GetMapping("/advanced-search")
    public ResponseEntity<List<Event>> advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start = null;
        LocalDateTime end = null;

        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDateTime.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDateTime.parse(endDate);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        List<Event> events = eventRepository.searchEvents(keyword, category, start, end);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> requestBody) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = requestBody.get("userId") != null 
                ? Long.parseLong(requestBody.get("userId").toString()) 
                : null;
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "กรุณาระบุผู้สร้างกิจกรรม");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "ไม่พบผู้ใช้งาน");
                return ResponseEntity.badRequest().body(response);
            }
            
            Event event = new Event();
            event.setName(requestBody.get("name").toString());
            event.setCategory(requestBody.get("category").toString());
            event.setDescription(requestBody.get("description").toString());
            event.setEventDate(LocalDateTime.parse(requestBody.get("eventDate").toString()));
            event.setMaxParticipants(Integer.parseInt(requestBody.get("maxParticipants").toString()));
            event.setCurrentParticipants(0);
            event.setStatus("OPEN");
            
            event.setOrganizer(userOpt.get());
            
            Event savedEvent = eventRepository.save(event);
            
            response.put("success", true);
            response.put("message", "สร้างกิจกรรมสำเร็จ");
            response.put("event", savedEvent);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "เกิดข้อผิดพลาด: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/my-events/{userId}")
    public ResponseEntity<List<Event>> getMyEvents(@PathVariable Long userId) {
        List<Event> events = eventRepository.findByOrganizerId(userId);
        return ResponseEntity.ok(events);
    }
}