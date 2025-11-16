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
    
    /**
     * ลงทะเบียนเข้าร่วมกิจกรรม
     * Test case 1: เมื่อมีที่ว่างเหลือ สามารถกดลงทะเบียนได้
     * Test case 5: บันทึกข้อมูลและลดจำนวนที่นั่ง
     * Test case 6: แสดงข้อความเมื่อเต็ม
     * Test case 7: แสดงข้อความเมื่อลงทะเบียนซ้ำ
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerEvent(
            @RequestParam Long userId, 
            @RequestParam Long eventId) {
        
        Map<String, Object> response = new HashMap<>();
        
        // ตรวจสอบว่า User และ Event มีอยู่จริง
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        
        if (!userOpt.isPresent() || !eventOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "ไม่พบผู้ใช้หรือกิจกรรม");
            return ResponseEntity.badRequest().body(response);
        }
        
        User user = userOpt.get();
        Event event = eventOpt.get();
        
        // Test case 7: ตรวจสอบว่าลงทะเบียนแล้วหรือยัง
        Optional<Registration> existingReg = registrationRepository
                .findByUserIdAndEventId(userId, eventId);
        
        if (existingReg.isPresent()) {
            response.put("success", false);
            response.put("message", "คุณได้ลงทะเบียนแล้ว");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Test case 6: ตรวจสอบว่าที่นั่งเต็มหรือไม่
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            response.put("success", false);
            response.put("message", "จำนวนที่นั่งเต็ม");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Test case 1 & 5: บันทึกการลงทะเบียนและอัปเดตจำนวนผู้เข้าร่วม
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus("REGISTERED");
        
        registrationRepository.save(registration);
        
        // อัปเดตจำนวนผู้เข้าร่วมปัจจุบัน
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        
        // เปลี่ยนสถานะเป็น FULL ถ้าเต็ม
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            event.setStatus("FULL");
        }
        
        eventRepository.save(event);
        
        response.put("success", true);
        response.put("message", "ลงทะเบียนสำเร็จ");
        response.put("registration", registration);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ยกเลิกการลงทะเบียน
     * Test case 2: เมื่อลงทะเบียนแล้ว สามารถยกเลิกได้
     */
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
        
        // ลบการลงทะเบียน
        registrationRepository.delete(registration);
        
        // ลดจำนวนผู้เข้าร่วม
        event.setCurrentParticipants(event.getCurrentParticipants() - 1);
        
        // เปลี่ยนสถานะกลับเป็น OPEN ถ้ายังไม่เต็ม
        if (event.getCurrentParticipants() < event.getMaxParticipants()) {
            event.setStatus("OPEN");
        }
        
        eventRepository.save(event);
        
        response.put("success", true);
        response.put("message", "ยกเลิกการลงทะเบียนสำเร็จ");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ดูรายการกิจกรรมที่ลงทะเบียนไว้
     * Test case 1: ระบบแสดงรายการกิจกรรมที่ผู้ใช้ลงทะเบียนไว้ทั้งหมด
     * Test case 2: แต่ละกิจกรรมมีการแสดงสถานะที่ชัดเจน
     */
    @GetMapping("/my-registrations/{userId}")
    public ResponseEntity<List<Registration>> getMyRegistrations(@PathVariable Long userId) {
        List<Registration> registrations = registrationRepository.findByUserId(userId);
        return ResponseEntity.ok(registrations);
    }
    
    /**
     * ตรวจสอบว่าผู้ใช้ลงทะเบียนกิจกรรมนี้แล้วหรือยัง
     */
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
}
