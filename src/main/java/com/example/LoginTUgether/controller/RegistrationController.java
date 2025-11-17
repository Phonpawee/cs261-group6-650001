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

import java.util.*;

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

    // ลงทะเบียน
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam Long userId,
            @RequestParam Long eventId) {

        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Event> eventOpt = eventRepository.findById(eventId);

        if (!userOpt.isPresent() || !eventOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "ไม่พบข้อมูล");
            return ResponseEntity.badRequest().body(response);
        }

        Event event = eventOpt.get();

        // เช็คซ้ำ
        if (registrationRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            response.put("success", false);
            response.put("message", "คุณได้ลงทะเบียนแล้ว");
            return ResponseEntity.badRequest().body(response);
        }

        // เช็คเต็ม
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            response.put("success", false);
            response.put("message", "กิจกรรมเต็มแล้ว");
            return ResponseEntity.badRequest().body(response);
        }

        Registration reg = new Registration();
        reg.setUser(userOpt.get());
        reg.setEvent(event);
        reg.setStatus("REGISTERED");
        registrationRepository.save(reg);

        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            event.setStatus("FULL");
        }
        eventRepository.save(event);

        response.put("success", true);
        response.put("message", "ลงทะเบียนสำเร็จ");
        return ResponseEntity.ok(response);
    }

    // ยกเลิกการลงทะเบียน
    @DeleteMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancel(
            @RequestParam Long userId,
            @RequestParam Long eventId) {

        Map<String, Object> response = new HashMap<>();

        Optional<Registration> regOpt = registrationRepository.findByUserIdAndEventId(userId, eventId);
        if (!regOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "ไม่พบข้อมูลการลงทะเบียน");
            return ResponseEntity.badRequest().body(response);
        }

        Registration reg = regOpt.get();
        Event event = reg.getEvent();

        registrationRepository.delete(reg);

        event.setCurrentParticipants(event.getCurrentParticipants() - 1);
        if (event.getCurrentParticipants() < event.getMaxParticipants()) {
            event.setStatus("OPEN");
        }
        eventRepository.save(event);

        response.put("success", true);
        response.put("message", "ยกเลิกสำเร็จ");
        return ResponseEntity.ok(response);
    }

    // รายชื่อกิจกรรมที่ลงทะเบียนไว้
    @GetMapping("/my-registrations/{userId}")
    public ResponseEntity<List<Registration>> myRegistrations(@PathVariable Long userId) {
        return ResponseEntity.ok(registrationRepository.findByUser_Id(userId));
    }

    // ⭐ Admin ดูรายชื่อผู้ลงทะเบียนในกิจกรรม ⭐
    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<Registration>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationRepository.findByEventId(eventId));
    }
}

