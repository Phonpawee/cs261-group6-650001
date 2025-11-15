package com.example.LoginTUgether.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.LoginTUgether.model.Activity;
import com.example.LoginTUgether.repo.ActivityRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public Activity createActivity(Activity activity) {
        // basic validation already via DTO; ensure status default
        activity.setStatus("OPEN");
        Activity saved = activityRepository.save(activity);

        // notify students (stub)
        //((Object) notificationService).notifyAllStudents(
            //"กิจกรรมใหม่: " + saved.getName(),
            //"/events/" + saved.getId()
        //);

        return saved;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Activity getActivityById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));
    }

    // soft-cancel: change status to CANCELLED
    public Activity cancelActivity(Long id) {
        java.util.Optional<Activity> opt = activityRepository.findById(id);
        if (opt.isEmpty()) throw new EntityNotFoundException("Activity not found");
        Activity a = opt.get();
        a.setStatus("CANCELLED");
        Activity saved = activityRepository.save(a);


        // notify participants/students about cancellation
        //((Object) notificationService).notifyAllStudents(
            //"กิจกรรมถูกยกเลิก: " + saved.getName(),
            //"/events/" + saved.getId()
        //);

        return saved;
    }

    // for admin maybe hard delete (optional)
    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) throw new EntityNotFoundException("Activity not found");
        activityRepository.deleteById(id);
    }
}