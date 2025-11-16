package com.example.LoginTUgether.dto;

public class CreateNotificationRequest {
    public String userId;
    public String title;
    public String message;
    public String type; // INFO / REMINDER / UPDATE / CANCELLED (optional)
}

