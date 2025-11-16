package com.example.LoginTUgether.service;

import com.example.LoginTUgether.model.Notification;
import com.example.LoginTUgether.model.NotificationType;
import com.example.LoginTUgether.repo.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Notification create(String userId, String title, String message, NotificationType type) {
        Notification n = new Notification(userId, title, message, type);
        return repo.save(n);
    }

    @Transactional(readOnly = true)
    public List<Notification> list(String userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Notification markRead(Long id) {
        Notification n = repo.findById(id).orElseThrow();
        n.setRead(true);
        return repo.save(n);
    }
}
