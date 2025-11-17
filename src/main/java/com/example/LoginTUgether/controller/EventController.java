package com.example.LoginTUgether.controller;

import com.example.LoginTUgether.model.Event;
import com.example.LoginTUgether.model.User;
import com.example.LoginTUgether.repo.EventRepository;
import com.example.LoginTUgether.repo.RegistrationRepository;
import com.example.LoginTUgether.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

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
        return ResponseEntity.ok(eventRepository.findByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String keyword) {
        return ResponseEntity.ok(eventRepository.findByNameContainingIgnoreCase(keyword));
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> requestBody) {

        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = Long.parseLong(requestBody.get("userId").toString());
            Optional<User> userOpt = userRepository.findById(userId);

            if (!userOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "ไม่พบผู้ใช้");
                return ResponseEntity.badRequest().body(response);
            }

            Event event = new Event();
            event.setName(requestBody.get("name").toString());
            event.setCategory(requestBody.get("category").toString());
            event.setDescription(requestBody.get("description").toString());
            event.setEventDate(LocalDateTime.parse(requestBody.get("eventDate").toString()));
            event.setMaxParticipants(Integer.parseInt(requestBody.get("maxParticipants").toString()));
            event.setStatus("OPEN");
            event.setCurrentParticipants(0);
            event.setOrganizer(userOpt.get());

            eventRepository.save(event);

            response.put("success", true);
            response.put("message", "สร้างกิจกรรมสำเร็จ");
            response.put("event", event);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "เกิดข้อผิดพลาด: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/my-events/{userId}")
    public ResponseEntity<List<Event>> getMyEvents(@PathVariable Long userId) {
        return ResponseEntity.ok(eventRepository.findByOrganizerId(userId));
    }

    // ============================================
    //  ⭐⭐ DELETE EVENT — ลบถาวร (Admin)
    // ============================================
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Map<String, Object>> deleteEvent(@PathVariable Long eventId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            if (!eventOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "ไม่พบกิจกรรม");
                return ResponseEntity.badRequest().body(response);
            }

            // Delete all registrations for this event
            registrationRepository.deleteByEventId(eventId);

            // Delete event
            eventRepository.deleteById(eventId);

            response.put("success", true);
            response.put("message", "ลบกิจกรรมสำเร็จ");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "เกิดข้อผิดพลาด: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
