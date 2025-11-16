package com.example.LoginTUgether.dto;

public class RegistrationSummaryDTO {
    private Long id;
    private Long userId;
    private Long eventId;
    private String status;
    private String note;
    private java.time.LocalDateTime registeredAt;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getEventId() {
        return eventId;
    }
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public java.time.LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
    public void setRegisteredAt(java.time.LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
