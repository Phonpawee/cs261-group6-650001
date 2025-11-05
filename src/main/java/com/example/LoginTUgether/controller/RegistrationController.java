package com.example.LoginTUgether.controller;

import com.example.LoginTUgether.model.Event;
import com.example.LoginTUgether.model.Registration;
import com.example.LoginTUgether.model.User;
import com.example.LoginTUgether.repo.EventRepository;
import com.example.LoginTUgether.repo.RegistrationRepository;
import com.example.LoginTUgether.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {
    
    @Autowired
    private RegistrationRepository registrationRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerEvent(
            @RequestParam Long userId, 
            @RequestParam Long eventId) {
        
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        
        if (!userOpt.isPresent() || !eventOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "User หรือ Event ไม่พบ");
            return ResponseEntity.badRequest().body(response);
        }
        
        User user = userOpt.get();
        Event event = eventOpt.get();
        
        Optional<Registration> existingReg = registrationRepository
                .findByUserIdAndEventId(userId, eventId);
        
        if (existingReg.isPresent()) {
            response.put("success", false);
            response.put("message", "คุณได้ลงทะเบียนแล้ว");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            response.put("success", false);
            response.put("message", "จำนวนที่นั่งเต็ม");
            return ResponseEntity.badRequest().body(response);
        }
        
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus("REGISTERED");
        
        registrationRepository.save(registration);
        
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            event.setStatus("FULL");
        }
        
        eventRepository.save(event);
        
        response.put("success", true);
        response.put("message", "ลงทะเบียนสำเร็จ");
        response.put("registration", registration);
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancelRegistration(
            @RequestParam Long userId, 
            @RequestParam Long eventId) {
        
        Map<String, Object> response = new HashMap<>();
        
        Optional<Registration> regOpt = registrationRepository
                .findByUserIdAndEventId(userId, eventId);
        
        if (!regOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "ไม่พบการลงทะเบียน");
            return ResponseEntity.badRequest().body(response);
        }
        
        Registration registration = regOpt.get();
        Event event = registration.getEvent();
        
        registrationRepository.delete(registration);
        
        event.setCurrentParticipants(event.getCurrentParticipants() - 1);
        
        if (event.getCurrentParticipants() < event.getMaxParticipants()) {
            event.setStatus("OPEN");
        }
        
        eventRepository.save(event);
        
        response.put("success", true);
        response.put("message", "ยกเลิกการลงทะเบียนสำเร็จ");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-registrations/{userId}")
    public ResponseEntity<List<Registration>> getMyRegistrations(@PathVariable Long userId) {
        List<Registration> registrations = registrationRepository.findByUserId(userId);
        return ResponseEntity.ok(registrations);
    }
    
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkRegistration(
            @RequestParam Long userId, 
            @RequestParam Long eventId) {
        
        Map<String, Object> response = new HashMap<>();
        
        Optional<Registration> regOpt = registrationRepository
                .findByUserIdAndEventId(userId, eventId);
        
        response.put("isRegistered", regOpt.isPresent());
        if (regOpt.isPresent()) {
            response.put("registration", regOpt.get());
        }
        
        return ResponseEntity.ok(response);
    }
    public Integer getMaxParticipants() {
        return getMaxParticipants();
    }
}