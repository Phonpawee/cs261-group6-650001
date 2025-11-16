package com.example.LoginTUgether.controller;

import com.example.LoginTUgether.dto.CreateNotificationRequest;
import com.example.LoginTUgether.dto.MarkReadRequest;
import com.example.LoginTUgether.model.Notification;
import com.example.LoginTUgether.model.NotificationType;
import com.example.LoginTUgether.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody CreateNotificationRequest req) {
        NotificationType type = NotificationType.INFO;
        if (req.type != null) {
            try { type = NotificationType.valueOf(req.type.toUpperCase()); } catch (Exception ignored) {}
        }
        Notification n = service.create(req.userId, req.title, req.message, type);
        return ResponseEntity.created(URI.create("/api/notifications/" + n.getId())).body(n);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> list(@RequestParam String userId) {
        return ResponseEntity.ok(service.list(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markRead(@PathVariable Long id,
                                                 @RequestBody(required = false) MarkReadRequest req) {
        return ResponseEntity.ok(service.markRead(id));
    }
}

